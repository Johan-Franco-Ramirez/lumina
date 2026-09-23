package com.example.app1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin
import com.example.app1.domain.model.BookType

/**
 * ENTIDAD PARA CACHÉ (Carga Instantánea)
 * Guarda temporalmente los libros del inicio para que aparezcan de inmediato.
 */
@Entity(tableName = "cached_books")
data class CachedBookEntity(
    @PrimaryKey val id: String,
    val category: String, // "trending", "mystery", "adventure", "free"
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
    val type: BookType,
    val readUrl: String?,
    val timestamp: Long = System.currentTimeMillis()
)

fun CachedBookEntity.toDomain() = Book(
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
    readUrl = readUrl
)

fun Book.toCachedEntity(category: String) = CachedBookEntity(
    id = id,
    category = category,
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
    readUrl = readUrl
)
