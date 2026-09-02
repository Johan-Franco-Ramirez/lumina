package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.api.GoogleBooksService
import com.example.app1.data.api.GutendexClient
import com.example.app1.data.database.LuminaDatabase
import com.example.app1.data.repository.BookRepository
import com.example.app1.data.repository.GutendexRepository
import com.example.app1.domain.model.Book
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ESTADO DE LA BÚSQUEDA (SearchUiState)
 */
sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<Book>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

/**
 * SEARCH VIEWMODEL
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LuminaDatabase.getDatabase(application)
    private val repository = BookRepository(
        apiService = GoogleBooksService.create(),
        libraryDao = database.libraryDao(),
    )
    private val gutendexRepository = GutendexRepository(GutendexClient.service)

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    /**
     * Búsqueda dinámica con Debouncing
     */
    fun onSearchQueryChanged(query: String, filter: String = "") {
        searchJob?.cancel()
        
        if (query.length < 3 && filter.isEmpty()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            _uiState.value = SearchUiState.Loading
            
            try {
                val fullQuery = if (filter.isNotEmpty()) "$query subject:$filter" else query
                
                // Realizamos búsquedas en paralelo
                val googleResults = repository.searchBooks(fullQuery)
                
                // Buscamos en Gutendex (priorizando español, luego global)
                var gutendexResults = gutendexRepository.searchBooks(query, languages = "es")
                if (gutendexResults.isEmpty()) {
                    gutendexResults = gutendexRepository.searchBooks(query)
                }
                
                // Combinamos y priorizamos (primero Google, luego Gutendex)
                val combinedResults = (googleResults + gutendexResults)
                    .distinctBy { "${it.title.lowercase()}_${it.author.lowercase()}" }
                
                if (combinedResults.isEmpty()) {
                    _uiState.value = SearchUiState.Error("No se encontraron pergaminos con ese nombre.")
                } else {
                    _uiState.value = SearchUiState.Success(combinedResults)
                }
            } catch (_: Exception) {
                _uiState.value = SearchUiState.Error("Error al consultar el catálogo remoto.")
            }
        }
    }
}
