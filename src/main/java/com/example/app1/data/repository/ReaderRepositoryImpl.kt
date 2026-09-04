package com.example.app1.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.example.app1.domain.model.BookSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

class ReaderRepositoryImpl(
    private val context: Context
) : ReaderRepository {

    override fun loadBookPages(source: BookSource): Flow<List<Bitmap>> = flow {
        val pages = when (source) {
            is BookSource.Local -> parseLocalFile(source.uri)
            is BookSource.Remote -> fetchRemoteFile(source.url, source.isStreaming)
        }
        emit(pages)
    }

    private suspend fun parseLocalFile(uri: Uri): List<Bitmap> = withContext(Dispatchers.IO) {
        val fileName = getFileName(uri) ?: "temp_file"

        try {
            when {
                // Caso 1: Cómics y Mangas (.cbz o .zip)
                fileName.endsWith(".cbz", ignoreCase = true) || fileName.endsWith(".zip", ignoreCase = true) -> {
                    parseCbzFile(uri)
                }
                // Caso 2: Documentos y Libros (.pdf)
                fileName.endsWith(".pdf", ignoreCase = true) -> {
                    parsePdfFile(uri)
                }
                else -> emptyList() // Formato no soportado
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // --- PROCESAMIENTO DE ARCHIVOS .CBZ / .ZIP ---
    private fun parseCbzFile(uri: Uri): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        inputStream?.use { stream ->
            ZipInputStream(stream).use { zipStream ->
                var entry = zipStream.nextEntry
                while (entry != null) {
                    // Ignoramos carpetas del sistema MacOS (__MACOSX) y subcarpetas vacías
                    if (!entry.isDirectory && !entry.name.contains("__MACOSX")) {
                        val name = entry.name.lowercase()
                        if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")) {
                            // Decodificamos la imagen directamente desde el flujo de bytes comprimido
                            val bitmap = BitmapFactory.decodeStream(zipStream)
                            if (bitmap != null) {
                                bitmaps.add(bitmap)
                            }
                        }
                    }
                    zipStream.closeEntry()
                    entry = zipStream.nextEntry
                }
            }
        }
        return bitmaps
    }

    // --- PROCESAMIENTO DE ARCHIVOS .PDF ---
    private fun parsePdfFile(uri: Uri): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()

        // Android SAF requiere copiar temporalmente el archivo PDF para obtener un FileDescriptor de lectura directa
        val tempFile = File(context.cacheDir, "temp_reader_file.pdf")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        val fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val pageCount = pdfRenderer.pageCount

        for (i in 0 until pageCount) {
            val page = pdfRenderer.openPage(i)

            // Creamos un Canvas del tamaño de la página del PDF en alta resolución (puedes ajustar el multiplicador)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            // Renderizamos el contenido del PDF dentro de nuestro mapa de bits vacío
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmaps.add(bitmap)

            page.close()
        }

        pdfRenderer.close()
        fileDescriptor.close()
        tempFile.delete() // Limpiamos la caché inmediatamente

        return bitmaps
    }

    // --- AYUDANTE PARA LEER EL NOMBRE DEL ARCHIVO ---
    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) name = it.getString(index)
                }
            }
        }
        return name ?: uri.path?.substringAfterLast('/')
    }

    private suspend fun fetchRemoteFile(url: String, isStreaming: Boolean): List<Bitmap> = withContext(Dispatchers.IO) {
        // En blanco por ahora para enfocarnos en los archivos locales que subas
        emptyList()
    }
}
