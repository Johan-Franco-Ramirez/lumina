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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        apiService = OpenLibraryService.create(application.cacheDir),
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
                coroutineScope {
                    // Lanzamos todas las peticiones en paralelo
                    val recommended = repository.getRecommendedBooks()
                    
                    val trendingDeferred = async { repository.getTrendingBooks() }
                    val continueReadingDeferred = async { repository.getLibraryBooks(ReadingStatus.READING).first() }
                    val freeClassicsDeferred = async { gutendexRepository.fetchSpanishBooks() }
                    val mysteryDeferred = async { repository.searchBooks("mystery") }
                    val adventureDeferred = async { repository.searchBooks("adventure") }
                    val sciFiDeferred = async { repository.searchBooks("sci-fi") }

                    // Esperamos los resultados (esto reduce el tiempo total al tiempo de la llamada más lenta)
                    val trending = trendingDeferred.await().take(15)
                    val continueReading = continueReadingDeferred.await().take(8)
                    val freeClassics = freeClassicsDeferred.await().take(20)
                    val mystery = mysteryDeferred.await().take(15)
                    val adventure = adventureDeferred.await().take(15)
                    val sciFi = sciFiDeferred.await().take(15)
                    
                    val featured = (trending + freeClassics + mystery).shuffled().firstOrNull()

                    _uiState.value = HomeUiState.Success(
                        featuredBook = featured ?: recommended.firstOrNull(),
                        trendingBooks = (recommended + trending).distinctBy { it.id },
                        continueReading = continueReading,
                        freeClassics = freeClassics,
                        mysteryBooks = mystery,
                        adventureBooks = adventure + sciFi
                    )
                }
            } catch (_: Exception) {
                _uiState.value = HomeUiState.Error("No se pudo conectar a la Biblioteca de Alejandría.")
            }
        }
    }
}
