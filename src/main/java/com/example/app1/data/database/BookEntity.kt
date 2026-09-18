package com.example.app1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin
import com.example.app1.domain.model.BookType

/**
 * ENTIDAD DE LIBRO (BookEntity)
 * 
 * Almacena de forma permanente los datos de los libros, cómics, mangas o webtoons.
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String?,
    val genres: List<String>,
    val targetAudience: String,
    val ageRange: String,
    val isIllustrated: Boolean,
    val rating: Double?,
    val origin: BookOrigin,
    val type: BookType = BookType.LIBRO,
    val pdfUri: String? = null,
    val readUrl: String? = null
)

/**
 * MAPPER: De Entidad a Dominio
 */
fun BookEntity.toDomain() = Book(
    id = id,
    title = title,
    author = author,
    description = description,
    coverUrl = coverUrl,
    genres = genres,
    targetAudience = targetAudience,
    ageRange = ageRange,
    isIllustrated = isIllustrated,
    rating = rating,
    origin = origin,
    type = type,
    pdfUri = pdfUri,
    readUrl = readUrl
)

/**
 * MAPPER: De Dominio a Entidad
 */
fun Book.toEntity() = BookEntity(
    id = id,
    title = title,
    author = author,
    description = description,
    coverUrl = coverUrl,
    genres = genres,
    targetAudience = targetAudience,
    ageRange = ageRange,
    isIllustrated = isIllustrated,
    rating = rating,
    origin = origin,
    type = type,
    pdfUri = pdfUri,
    readUrl = readUrl
)
