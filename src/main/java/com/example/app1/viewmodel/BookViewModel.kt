package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.api.OpenLibraryService
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
        val freeClassics: List<Book> = emptyList(),
        val mysteryBooks: List<Book> = emptyList(),
        val adventureBooks: List<Book> = emptyList()
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
        apiService = OpenLibraryService.create(),
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
                
                // 2. Tendencias (Open Library)
                val trending = repository.getTrendingBooks().take(15)
                
                // 3. Libros que el usuario está leyendo actualmente (Room)
                val continueReading = repository.getLibraryBooks(ReadingStatus.READING).first().take(8)
                
                // 4. Clásicos gratuitos (Gutendex)
                val freeClassics = gutendexRepository.fetchSpanishBooks().take(20)

                // 5. Categorías extra (Open Library)
                val mystery = repository.searchBooks("mystery").take(15)
                val adventure = repository.searchBooks("adventure").take(15)
                val sciFi = repository.searchBooks("sci-fi").take(15)
                
                // 6. Selección dinámica para "Para ti" (Libro destacado del día)
                val featured = (trending + freeClassics + mystery).shuffled().firstOrNull()

                _uiState.value = HomeUiState.Success(
                    featuredBook = featured ?: recommended.firstOrNull(),
                    trendingBooks = (recommended + trending).distinctBy { it.id },
                    continueReading = continueReading,
                    freeClassics = freeClassics,
                    mysteryBooks = mystery,
                    adventureBooks = adventure + sciFi
                )
            } catch (_: Exception) {
                _uiState.value = HomeUiState.Error("No se pudo conectar a la Biblioteca de Alejandría.")
            }
        }
    }
}
