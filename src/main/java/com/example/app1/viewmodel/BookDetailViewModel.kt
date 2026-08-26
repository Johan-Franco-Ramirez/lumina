package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.api.GoogleBooksService
import com.example.app1.data.database.LuminaDatabase
import com.example.app1.data.database.ReadingStatus
import com.example.app1.data.repository.BookRepository
import com.example.app1.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ESTADO DEL DETALLE DEL LIBRO
 */
sealed class BookDetailUiState {
    object Loading : BookDetailUiState()
    data class Success(
        val book: Book,
        val libraryStatus: ReadingStatus? = null,
    ) : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
}

/**
 * BOOK DETAIL VIEWMODEL
 */
class BookDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LuminaDatabase.getDatabase(application)
    private val repository = BookRepository(
        apiService = GoogleBooksService.create(),
        libraryDao = database.libraryDao()
    )

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            val book = repository.getBookById(bookId)
            val status = repository.getBookLibraryStatus(bookId)
            
            if (book != null) {
                _uiState.value = BookDetailUiState.Success(book, status)
            } else {
                _uiState.value = BookDetailUiState.Error("No se encontró la obra en nuestros archivos.")
            }
        }
    }

    /**
     * ACCIÓN: Cambiar el estado de lectura (Quiero leer, Leyendo, Leído)
     */
    fun updateReadingStatus(book: Book, status: ReadingStatus) {
        viewModelScope.launch {
            repository.saveBookToLibrary(book, status)
            // Actualizamos el estado local para que el botón cambie visualmente
            (uiState.value as? BookDetailUiState.Success)?.let {
                _uiState.value = it.copy(libraryStatus = status)
            }
        }
    }
}
