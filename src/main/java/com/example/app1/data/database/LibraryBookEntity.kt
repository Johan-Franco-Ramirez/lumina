package com.example.app1.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ESTADOS DE LECTURA
 */
enum class ReadingStatus {
    WANT_TO_READ,
    READING,
    READ
}

/**
 * ENTIDAD DE LA BIBLIOTECA (LibraryBookEntity)
 * 
 * ¿Qué es?
 * Representa la relación entre el usuario y un libro.
 * 
 * ¿Para qué sirve?
 * Guarda el progreso del usuario: si quiere leer el libro, si lo está leyendo 
 * actualmente o si ya lo terminó.
 */
@Entity(tableName = "library")
data class LibraryBookEntity(
    @PrimaryKey val bookId: String,
    val status: ReadingStatus,
    val addedDate: Long = System.currentTimeMillis()
)
