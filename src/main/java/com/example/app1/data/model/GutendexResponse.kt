package com.example.app1.data.model

import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin
import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de la API de Gutendex
 */
data class GutendexResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<GutendexBook>
)

/**
 * DTO para un libro individual en Gutendex
 */
data class GutendexBook(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("authors") val authors: List<GutendexAuthor>,
    @SerializedName("languages") val languages: List<String>,
    @SerializedName("download_count") val downloadCount: Int,
    @SerializedName("formats") val formats: Map<String, String>
) {
    /**
     * URL de la portada del libro.
     * Busca la clave "image/jpeg" en el mapa de formatos.
     */
    val coverUrl: String?
        get() = formats["image/jpeg"]

    /**
     * URL del contenido del libro (HTML o Texto plano).
     * Prioriza HTML para una mejor experiencia de lectura.
     */
    val textUrl: String?
        get() = formats["text/html"] ?: formats["text/plain; charset=us-ascii"] ?: formats["text/plain"]

    /**
     * Mapea el DTO de Gutendex al modelo de dominio Book.
     */
    fun toDomain() = Book(
        id = "GUTEN_$id",
        title = title,
        author = authors.joinToString(", ") { it.name },
        description = "Libro de dominio público del Proyecto Gutenberg. Conteo de descargas: $downloadCount.",
        coverUrl = coverUrl,
        genres = listOf("Dominio Público", "Clásico"),
        targetAudience = "Público General",
        ageRange = "Todas las edades",
        isIllustrated = false,
        rating = null,
        origin = BookOrigin.GUTENDEX,
        readUrl = textUrl
    )
}

/**
 * DTO para el autor de un libro en Gutendex
 */
data class GutendexAuthor(
    @SerializedName("name") val name: String,
    @SerializedName("birth_year") val birthYear: Int?,
    @SerializedName("death_year") val deathYear: Int?
)
