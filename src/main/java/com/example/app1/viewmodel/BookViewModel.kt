package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.api.GoogleBooksService
import com.example.app1.data.api.GutendexClient
import com.example.app1.data.database.LuminaDatabase
import com.example.app1.data.database.ReadingStatus
import com.example.app1.data.repository.BookRepository
import com.example.app1.data.repository.GutendexRepository
import com.example.app1.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ESTADO DE LA PANTALLA (HomeUiState)
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val featuredBook: Book?,
        val trendingBooks: List<Book>,
        val continueReading: List<Book> = emptyList(),
        val freeClassics: List<Book> = emptyList()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * BOOK VIEWMODEL
 * 
 * Usamos AndroidViewModel para tener acceso al Context y así iniciar la base de datos.
 */
class BookViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = LuminaDatabase.getDatabase(application)
    private val repository = BookRepository(
        apiService = GoogleBooksService.create(),
        libraryDao = database.libraryDao()
    )
    private val gutendexRepository = GutendexRepository(GutendexClient.service)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // 1. Recomendados estáticos
                val recommended = repository.getRecommendedBooks()
                
                // 2. Tendencias (Google Books)
                val trending = repository.getTrendingBooks().take(10)
                
                // 3. Libros que el usuario está leyendo actualmente (Room)
                val continueReading = repository.getLibraryBooks(ReadingStatus.READING).first().take(5)
                
                // 4. Clásicos gratuitos (Gutendex)
                val freeClassics = gutendexRepository.fetchSpanishBooks().take(10)
                
                _uiState.value = HomeUiState.Success(
                    featuredBook = recommended.firstOrNull(),
                    trendingBooks = (recommended.drop(1) + trending).distinctBy { it.id },
                    continueReading = continueReading,
                    freeClassics = freeClassics
                )
            } catch (_: Exception) {
                _uiState.value = HomeUiState.Error("No se pudo conectar a la Biblioteca de Alejandría.")
            }
        }
    }
}
