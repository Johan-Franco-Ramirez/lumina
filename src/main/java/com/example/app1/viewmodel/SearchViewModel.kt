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
     * Búsqueda manual con filtros aplicados
     */
    fun performSearch(query: String, genre: String?, ageRange: String?, isIllustrated: Boolean) {
        searchJob?.cancel()
        
        if (query.isEmpty() && genre == null && ageRange == null && !isIllustrated) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            try {
                // Construir query para APIs (Google Books permite filtrar por subject)
                val apiQuery = if (!genre.isNullOrEmpty()) "$query subject:$genre" else query
                
                // 1. Google Books
                val googleResults = repository.searchBooks(apiQuery)
                
                // 2. Gutendex (priorizando español si hay query)
                var gutendexResults = if (query.isNotEmpty()) {
                    val esResults = gutendexRepository.searchBooks(query, languages = "es")
                    if (esResults.isEmpty()) gutendexRepository.searchBooks(query) else esResults
                } else if (!genre.isNullOrEmpty()) {
                    gutendexRepository.searchBooks(genre)
                } else {
                    emptyList()
                }
                
                // 3. Combinar resultados únicos
                var results = (googleResults + gutendexResults)
                    .distinctBy { "${it.title.lowercase()}_${it.author.lowercase()}" }

                // 4. Filtros locales (Edad e Ilustraciones)
                if (!ageRange.isNullOrEmpty()) {
                    results = results.filter { 
                        it.targetAudience.contains(ageRange, ignoreCase = true) || 
                        it.ageRange.contains(ageRange, ignoreCase = true)
                    }
                }
                
                if (isIllustrated) {
                    results = results.filter { it.isIllustrated }
                }

                if (results.isEmpty()) {
                    _uiState.value = SearchUiState.Error("No se encontraron pergaminos con esos criterios.")
                } else {
                    _uiState.value = SearchUiState.Success(results)
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error("Error al consultar el catálogo remoto.")
            }
        }
    }

    /**
     * Búsqueda dinámica con Debouncing (opcional, para uso futuro)
     */
    fun onSearchQueryChanged(query: String, filter: String = "") {
        searchJob?.cancel()
        
        if (query.length < 3 && filter.isEmpty()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            performSearch(query, if (filter.isNotEmpty()) filter else null, null, false)
        }
    }
}
