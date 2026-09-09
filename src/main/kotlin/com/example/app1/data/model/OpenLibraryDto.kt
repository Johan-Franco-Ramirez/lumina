package com.example.app1.data.model

import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin
import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de búsqueda de Open Library
 */
data class OpenLibrarySearchResponse(
    @SerializedName("numFound") val numFound: Int,
    @SerializedName("start") val start: Int,
    @SerializedName("docs") val docs: List<OpenLibraryDoc>
)

/**
 * DTO para un documento (libro) en los resultados de búsqueda
 */
data class OpenLibraryDoc(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("author_name") val authorName: List<String>?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?,
    @SerializedName("cover_i") val coverId: Int?,
    @SerializedName("subject") val subject: List<String>?,
    @SerializedName("language") val languages: List<String>?
) {
    /**
     * Mapea el DTO de Open Library al modelo de dominio Book.
     */
    fun toDomain(): Book {
        val id = key.removePrefix("/works/").removePrefix("/authors/")
        val coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
        
        return Book(
            id = "OPEN_$id",
            title = title,
            author = authorName?.joinToString(", ") ?: "Autor desconocido",
            description = "Publicado originalmente en ${firstPublishYear ?: "año desconocido"}. Temas: ${subject?.take(3)?.joinToString(", ") ?: "General"}.",
            coverUrl = coverUrl,
            genres = subject?.take(5) ?: listOf("General"),
            targetAudience = "Público General",
            ageRange = "Todas las edades",
            isIllustrated = false,
            rating = null,
            origin = BookOrigin.REMOTE,
            readUrl = "https://openlibrary.org$key"
        )
    }
}

/**
 * DTO para el detalle de un "Work" (Obra) en Open Library
 */
data class OpenLibraryWork(
    @SerializedName("description") val description: Any?, // Puede ser String o un objeto {"value": "..."}
    @SerializedName("title") val title: String,
    @SerializedName("covers") val covers: List<Int>?,
    @SerializedName("key") val key: String
) {
    fun getDescriptionText(): String {
        return when (description) {
            is String -> description
            is Map<*, *> -> description["value"] as? String ?: "Sin descripción disponible."
            else -> "Sin descripción disponible."
        }
    }
}
