package com.example.app1.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.example.app1.domain.model.BookSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

class ReaderRepositoryImpl(
    private val context: Context
) : ReaderRepository {

    private val MAX_PAGES = 500

    override fun loadBookPages(source: BookSource): Flow<List<Bitmap>> = flow {
        val pages = when (source) {
            is BookSource.Local -> parseLocalFile(source.uri)
            is BookSource.Collection -> {
                val allPages = mutableListOf<Bitmap>()
                source.uris.sortedBy { getFileName(it) }.forEach { uri ->
                    if (allPages.size < MAX_PAGES) allPages.addAll(parseLocalFile(uri))
                }
                allPages
            }
            is BookSource.Remote -> emptyList()
        }
        emit(pages)
    }

    private suspend fun parseLocalFile(uri: Uri): List<Bitmap> = withContext(Dispatchers.IO) {
        val fileName = getFileName(uri) ?: "doc"
        val ext = fileName.lowercase()

        try {
            when {
                ext.endsWith(".pdf") -> parsePdfFile(uri)
                ext.endsWith(".zip") || ext.endsWith(".cbz") || 
                ext.endsWith(".cbr") || ext.endsWith(".epub") -> parseCompressedFast(uri)
                isImage(fileName) -> listOfNotNull(parseSingleImage(uri))
                else -> emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun parseCompressedFast(uri: Uri): List<Bitmap> = withContext(Dispatchers.Default) {
        val bitmaps = mutableListOf<Bitmap>()
        val tempFile = File(context.cacheDir, "fast_read_${System.currentTimeMillis()}.tmp")
        
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output -> input.copyTo(output) }
            }

            ZipFile(tempFile).use { zip ->
                val entries = zip.entries().asSequence()
                    .filter { !it.isDirectory && isImage(it.name) && !it.name.contains("__MACOSX") }
                    .sortedBy { getNaturalOrderKey(it.name) }
                    .take(MAX_PAGES)
                    .toList()

                // CARGA EN PARALELO: Procesamos imágenes simultáneamente para ganar velocidad
                val deferredBitmaps = entries.map { entry ->
                    async(Dispatchers.Default) {
                        zip.getInputStream(entry).use { stream ->
                            val bytes = stream.readBytes()
                            if (bytes.isNotEmpty()) {
                                decodeResilient(bytes)
                            } else null
                        }
                    }
                }
                bitmaps.addAll(deferredBitmaps.awaitAll().filterNotNull())
            }
        } catch (e: Exception) {
        } finally {
            tempFile.delete()
        }
        bitmaps
    }

    // Intenta decodificar con diferentes configuraciones para maximizar compatibilidad
    private fun decodeResilient(bytes: ByteArray): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        return try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        } catch (e: Exception) {
            null
        }
    }

    private fun parsePdfFile(uri: Uri): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val tempFile = File(context.cacheDir, "pdf_tmp.pdf")
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output -> input.copyTo(output) }
            }
            val fd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fd)
            val count = Math.min(renderer.pageCount, MAX_PAGES)
            for (i in 0 until count) {
                val page = renderer.openPage(i)
                val bitmap = Bitmap.createBitmap((page.width * 1.2f).toInt(), (page.height * 1.2f).toInt(), Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.WHITE)
                val matrix = Matrix().apply { postScale(1.2f, 1.2f) }
                page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
            renderer.close()
            fd.close()
        } catch (e: Exception) { }
        finally { tempFile.delete() }
        return bitmaps
    }

    private fun parseSingleImage(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { 
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.RGB_565 }) 
            }
        } catch (e: Exception) { null }
    }

    private fun isImage(name: String): Boolean {
        val n = name.lowercase()
        return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || 
               n.endsWith(".webp") || n.endsWith(".jp2") || n.endsWith(".bmp")
    }

    private fun getNaturalOrderKey(name: String): String {
        return name.replace(Regex("\\d+")) { it.value.padStart(10, '0') }
    }

    private fun getFileName(uri: Uri): String? {
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) return it.getString(index)
                }
            }
        }
        return uri.path?.substringAfterLast('/')
    }
}
