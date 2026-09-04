package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.api.OpenLibraryService
import com.example.app1.data.database.LuminaDatabase
import com.example.app1.data.database.ReadingStatus
import com.example.app1.data.repository.BookRepository
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * LIBRARY VIEWMODEL
 */
class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = LuminaDatabase.getDatabase(application)
    private val repository = BookRepository(
        apiService = OpenLibraryService.create(),
        libraryDao = database.libraryDao()
    )

    // Flujos de datos reactivos de la base de datos
    val wantToReadBooks: Flow<List<Book>> = repository.getLibraryBooks(ReadingStatus.WANT_TO_READ)
    val readingBooks: Flow<List<Book>> = repository.getLibraryBooks(ReadingStatus.READING)
    val readBooks: Flow<List<Book>> = repository.getLibraryBooks(ReadingStatus.READ)

    /**
     * IMPORTAR LIBRO PERSONAL (PDF)
     * Guarda los metadatos introducidos por el usuario y la URI del archivo.
     */
    fun importPersonalBook(
        title: String,
        author: String,
        description: String,
        pdfUri: String
    ) {
        viewModelScope.launch {
            val newBook = Book(
                id = "LOCAL_${UUID.randomUUID()}", 
                title = title,
                author = author,
                description = description,
                coverUrl = null, 
                genres = listOf("Personal"),
                targetAudience = "Propio",
                ageRange = "No especificada",
                isIllustrated = false,
                rating = null,
                origin = BookOrigin.PERSONAL_PDF,
                pdfUri = pdfUri
            )
            repository.savePersonalBook(newBook)
        }
    }

    /**
     * ELIMINAR LIBRO
     */
    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            repository.deleteBookFromLibrary(bookId)
        }
    }
}
