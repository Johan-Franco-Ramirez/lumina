package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.app1.data.api.GoogleBooksService
import com.example.app1.data.database.LuminaDatabase
import com.example.app1.data.database.ReadingStatus
import com.example.app1.data.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * PROFILE VIEWMODEL
 * 
 * ¿Qué es?
 * El gestor de la información del usuario y sus estadísticas.
 * 
 * ¿Para qué sirve?
 * Calcula los totales de libros en cada categoría para mostrar un resumen 
 * del progreso del lector.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LuminaDatabase.getDatabase(application)
    private val repository = BookRepository(
        apiService = GoogleBooksService.create(),
        libraryDao = database.libraryDao(),
    )

    /**
     * ESTADÍSTICAS REACTIVAS
     * Usamos .map en el Flow para transformar la lista de libros en un simple número.
     * Si se agrega un libro a la biblioteca, estos números se actualizarán solos.
     */
    val wantToReadCount: Flow<Int> = repository.getLibraryBooks(ReadingStatus.WANT_TO_READ).map { it.size }
    val readingCount: Flow<Int> = repository.getLibraryBooks(ReadingStatus.READING).map { it.size }
    val readCount: Flow<Int> = repository.getLibraryBooks(ReadingStatus.READ).map { it.size }
}
