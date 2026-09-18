package com.example.app1.domain.model

/**
 * MODELO DE DATOS CENTRAL - Book.kt
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String?,
    val genres: List<String>,
    val targetAudience: String, // Ejemplo: "Infantil", "Juvenil", "Adultos"
    val ageRange: String,      // Ejemplo: "9-12", "18+"
    val isIllustrated: Boolean,
    val rating: Double?,
    val origin: BookOrigin = BookOrigin.REMOTE,
    val type: BookType = BookType.LIBRO, // Comic, Libro, Manga, Webtoon
    val pdfUri: String? = null, // Solo se llena si origin es PERSONAL_PDF
    val readUrl: String? = null  // URL para lectura online (Gutendex)
)

/**
 * TIPO DE OBRA / LIBRO
 */
enum class BookType(val displayName: String) {
    COMIC("Cómic"),
    LIBRO("Libro"),
    MANGA("Manga"),
    WEBTOON("Webtoon")
}

/**
 * ORIGEN DEL LIBRO
 */
enum class BookOrigin {
    REMOTE,       // Proviene de Google Books / Open Library API
    PERSONAL_PDF, // Subido por el usuario
    GUTENDEX      // Proviene de Gutendex (Dominio Público)
}
