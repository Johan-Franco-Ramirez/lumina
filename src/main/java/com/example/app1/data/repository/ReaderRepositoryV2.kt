package com.example.app1.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.app1.domain.model.BookSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

class ReaderRepositoryV2(private val context: Context) : ReaderRepository {

    private val MAX_PAGES = 1000

    override fun loadBookPages(source: BookSource): Flow<List<Bitmap>> = flow {
        val pages = when (source) {
            is BookSource.Local -> parseFile(source.uri)
            is BookSource.Collection -> {
                val list = mutableListOf<Bitmap>()
                source.uris.forEach { list.addAll(parseFile(it)) }
                list
            }
            is BookSource.Remote -> emptyList()
        }
        emit(pages)
    }

    private suspend fun parseFile(uri: Uri): List<Bitmap> = withContext(Dispatchers.IO) {
        val fileName = getFileName(uri).lowercase()
        try {
            when {
                fileName.endsWith(".pdf") -> parsePdf(uri)
                // Unificamos ZIP, CBZ, EPUB y CBR (si es base ZIP)
                fileName.endsWith(".zip") || fileName.endsWith(".cbz") || 
                fileName.endsWith(".cbr") || fileName.endsWith(".epub") -> parseArchive(uri)
                isImage(fileName) -> listOfNotNull(decodeImage(uri))
                else -> emptyList()
            }
        } catch (e: Exception) { emptyList() }
    }

    private fun parseArchive(uri: Uri): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val temp = File(context.cacheDir, "v2_temp_${System.currentTimeMillis()}")
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(temp).use { output -> input.copyTo(output) }
            }
            ZipFile(temp).use { zip ->
                zip.entries().asSequence()
                    .filter { !it.isDirectory && isImage(it.name) && !it.name.contains("__MACOSX") }
                    .sortedBy { it.name }
                    .take(MAX_PAGES)
                    .forEach { entry ->
                        zip.getInputStream(entry).use { stream ->
                            val bmp = BitmapFactory.decodeStream(stream, null, 
                                BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.RGB_565 })
                            if (bmp != null) bitmaps.add(bmp)
                        }
                    }
            }
        } catch (e: Exception) { } finally { temp.delete() }
        return bitmaps
    }

    private fun parsePdf(uri: Uri): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val temp = File(context.cacheDir, "v2_pdf.pdf")
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(temp).use { output -> input.copyTo(output) }
            }
            val fd = ParcelFileDescriptor.open(temp, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fd)
            val count = Math.min(renderer.pageCount, MAX_PAGES)
            for (i in 0 until count) {
                val page = renderer.openPage(i)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
            renderer.close()
            fd.close()
        } catch (e: Exception) { } finally { temp.delete() }
        return bitmaps
    }

    private fun decodeImage(uri: Uri): Bitmap? {
        return context.contentResolver.openInputStream(uri)?.use { 
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.RGB_565 })
        }
    }

    private fun isImage(name: String) = listOf(".jpg", ".jpeg", ".png", ".webp", ".jp2").any { name.lowercase().endsWith(it) }

    private fun getFileName(uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null, null)?.use {
            if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow("_display_name")) else ""
        } ?: uri.path?.substringAfterLast('/') ?: "file"
    }
}
