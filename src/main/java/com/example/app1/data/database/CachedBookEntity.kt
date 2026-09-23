package com.example.app1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin

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
    val origin: BookOrigin,
    val readUrl: String?,
    val readerType: String?,
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
    ageRange = "No especificada",
    isIllustrated = false,
    rating = null,
    origin = origin,
    readUrl = readUrl,
    readerType = readerType
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
    origin = origin,
    readUrl = readUrl,
    readerType = readerType
)
