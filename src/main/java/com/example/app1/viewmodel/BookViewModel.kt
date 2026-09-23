package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.api.GutendexClient
import com.example.app1.data.database.LuminaDatabase
import com.example.app1.data.database.ReadingStatus
import com.example.app1.data.repository.BookRepository
import com.example.app1.data.repository.GutendexRepository
import com.example.app1.domain.model.Book
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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
            // Añadimos un retraso artificial de 3 segundos para apreciar la pantalla de carga de inicio
            delay(3000)
            
            // Intentamos cargar caché primero para CARGA INSTANTÁNEA
            loadFromCache()
            
            // Luego buscamos actualizaciones en la API
            refreshFromApi()
        }
    }

    private suspend fun loadFromCache() {
        try {
            coroutineScope {
                val trending = async { repository.getCachedBooks("trending") }
                val mystery = async { repository.getCachedBooks("mystery") }
                val adventure = async { repository.getCachedBooks("adventure") }
                val free = async { repository.getCachedBooks("free") }

                val cachedTrending = trending.await()
                if (cachedTrending.isNotEmpty()) {
                    _uiState.value = HomeUiState.Success(
                        featuredBook = cachedTrending.shuffled().firstOrNull(),
                        trendingBooks = cachedTrending,
                        mysteryBooks = mystery.await(),
                        adventureBooks = adventure.await(),
                        freeClassics = free.await()
                    )
                }
            }
        } catch (_: Exception) {}
    }

    private suspend fun refreshFromApi() {
        try {
            // Recomendados estáticos (siempre disponibles)
            val recommended = repository.getRecommendedBooks()

            coroutineScope {
                // Lanzamos peticiones en paralelo con manejo de errores individual para cada una
                val trendingDeferred = async { safeApiCall { repository.getTrendingBooks() } }
                val continueReadingDeferred = async { safeApiCall { repository.getLibraryBooks(ReadingStatus.READING).first() } }
                val freeClassicsDeferred = async { safeApiCall { gutendexRepository.fetchSpanishBooks() } }
                val mysteryDeferred = async { safeApiCall { repository.searchBooks("mystery") } }
                val adventureDeferred = async { safeApiCall { repository.searchBooks("adventure") } }
                val sciFiDeferred = async { safeApiCall { repository.searchBooks("sci-fi") } }

                // Esperamos los resultados (si fallan, devuelven lista vacía gracias a safeApiCall)
                val trending = trendingDeferred.await() ?: emptyList()
                val continueReading = continueReadingDeferred.await() ?: emptyList()
                val freeClassics = freeClassicsDeferred.await() ?: emptyList()
                val mystery = mysteryDeferred.await() ?: emptyList()
                val adventure = adventureDeferred.await() ?: emptyList()
                val sciFi = sciFiDeferred.await() ?: emptyList()
                
                val allAdventure = (adventure + sciFi).distinctBy { it.id }
                val featured = (trending + freeClassics + mystery).shuffled().firstOrNull()

                // Actualizamos la UI
                _uiState.value = HomeUiState.Success(
                    featuredBook = featured ?: recommended.firstOrNull(),
                    trendingBooks = (recommended + trending).distinctBy { it.id },
                    continueReading = continueReading,
                    freeClassics = freeClassics,
                    mysteryBooks = mystery,
                    adventureBooks = allAdventure
                )

                // Guardamos en caché SOLO lo que realmente se descargó
                if (trending.isNotEmpty()) repository.saveBooksToCache(trending, "trending")
                if (mystery.isNotEmpty()) repository.saveBooksToCache(mystery, "mystery")
                if (allAdventure.isNotEmpty()) repository.saveBooksToCache(allAdventure, "adventure")
                if (freeClassics.isNotEmpty()) repository.saveBooksToCache(freeClassics, "free")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falló todo y no hay nada en pantalla, mostramos error
            if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Error("No se pudo conectar a la Biblioteca. Verifica tu internet.")
            }
        }
    }

    private suspend fun <T> safeApiCall(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
