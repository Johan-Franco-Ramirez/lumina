package com.example.app1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin

/**
 * ENTIDAD DE LIBRO (BookEntity)
 * 
 * ¿Qué es?
 * Una clase que representa una tabla en la base de datos SQLite.
 * 
 * ¿Para qué sirve?
 * Almacena de forma permanente los datos de los libros que el usuario 
 * ha consultado o guardado, evitando depender siempre de Internet.
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
    val isIllustrated: Boolean,
    val rating: Double?,
    val origin: BookOrigin
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
    ageRange = "No especificada",
    isIllustrated = isIllustrated,
    rating = rating,
    origin = origin
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
    isIllustrated = isIllustrated,
    rating = rating,
    origin = origin
)
