package com.example.app1.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.example.app1.domain.model.ReaderMode
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * CLASIFICACIÓN DE DESTINO
 */
enum class LuminaCategory(val folderName: String, val readerMode: ReaderMode) {
    LIBRO("Libros", ReaderMode.PDF),
    MANGA("Mangas", ReaderMode.MangaRTL),
    COMIC("Comics", ReaderMode.ComicLTR),
    WEBTOON("Webtoons", ReaderMode.Webtoon)
}

data class OrganizedFile(
    val nombre_archivo: String,
    val ruta_nueva: String,
    val formato_origen: String,
    val etiqueta_categoria: String,
    val modo_lectura_sugerido: String,
    val paginas_totales: Int
)

class LuminaOrganizer(private val context: Context) {

    private val keywordsManga = listOf("manga", "chapter", "capitulo", "volumen", "vol", "v0")
    private val keywordsWebtoon = listOf("webtoon", "manhwa", "tira", "longstrip")

    /**
     * FUNCIÓN PRINCIPAL: Escanea y organiza una carpeta
     */
    fun organizeFolder(rootFolder: File): List<OrganizedFile> {
        val organizedResults = mutableListOf<OrganizedFile>()
        val libraryBase = File(rootFolder, "Biblioteca")
        
        // Crear estructura de carpetas
        LuminaCategory.values().forEach { category ->
            File(libraryBase, category.folderName).mkdirs()
        }

        rootFolder.listFiles()?.forEach { file ->
            if (file.isFile && isSupported(file)) {
                val category = classifyFile(file)
                val destFolder = File(libraryBase, category.folderName)
                val newFile = File(destFolder, file.name)

                // Mover archivo
                if (file.renameTo(newFile)) {
                    organizedResults.add(
                        OrganizedFile(
                            nombre_archivo = newFile.name,
                            ruta_nueva = newFile.absolutePath,
                            formato_origen = newFile.extension.uppercase(),
                            etiqueta_categoria = category.name,
                            modo_lectura_sugerido = "ReaderMode.${category.readerMode.javaClass.simpleName}",
                            paginas_totales = countPages(newFile)
                        )
                    )
                }
            }
        }
        return organizedResults
    }

    private fun isSupported(file: File): Boolean {
        val extensions = listOf("pdf", "epub", "cbz", "zip", "jpg", "png", "webp")
        return file.extension.lowercase() in extensions
    }

    private fun classifyFile(file: File): LuminaCategory {
        val name = file.name.lowercase()
        val ext = file.extension.lowercase()

        // REGLA 1: Libros nativos (Epub, etc)
        if (ext == "epub") return LuminaCategory.LIBRO

        // REGLA 2: Análisis de PDF
        if (ext == "pdf") {
            // Nota de Novato: PdfRenderer no extrae texto fácilmente, 
            // así que usamos el tamaño como heurística profesional:
            // Si el archivo es pequeño pero tiene muchas páginas, suele ser texto.
            return if (file.length() < 5_000_000) LuminaCategory.LIBRO else LuminaCategory.COMIC
        }

        // REGLA 3: Análisis de Archivos Comprimidos (ZIP/CBZ)
        if (ext == "zip" || ext == "cbz") {
            if (keywordsWebtoon.any { name.contains(it) }) return LuminaCategory.WEBTOON
            
            // Prueba de fuego por resolución (Análisis interno)
            if (isVerticalStrip(file)) return LuminaCategory.WEBTOON
            
            if (keywordsManga.any { name.contains(it) }) return LuminaCategory.MANGA
        }

        return LuminaCategory.COMIC
    }

    private fun isVerticalStrip(file: File): Boolean {
        try {
            val zipFile = ZipFile(file)
            val entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (!entry.isDirectory && isImage(entry.name)) {
                    zipFile.getInputStream(entry).use { stream ->
                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeStream(stream, null, options)
                        // Si el alto es 3 veces el ancho, es un Webtoon claro
                        if (options.outHeight > (options.outWidth * 2.5)) return true
                    }
                    break // Solo probamos la primera imagen por eficiencia
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
        return false
    }

    private fun isImage(name: String) = listOf("jpg", "jpeg", "png", "webp").any { name.lowercase().endsWith(it) }

    private fun countPages(file: File): Int {
        return try {
            when (file.extension.lowercase()) {
                "pdf" -> {
                    val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(fd)
                    val count = renderer.pageCount
                    renderer.close()
                    fd.close()
                    count
                }
                "zip", "cbz" -> {
                    var count = 0
                    val zip = ZipFile(file)
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        if (isImage(entries.nextElement().name)) count++
                    }
                    count
                }
                else -> 1
            }
        } catch (e: Exception) { 0 }
    }
}
