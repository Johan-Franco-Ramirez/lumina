package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.api.GoogleBooksService
import com.example.app1.data.database.LuminaDatabase
import com.example.app1.data.repository.BookRepository
import com.example.app1.domain.model.Book
import kotlinx.coroutines.Job
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

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    /**
     * Búsqueda con filtros avanzados
     */
    fun performSearch(
        query: String,
        genre: String? = null,
        ageRange: String? = null,
        isIllustrated: Boolean = false
    ) {
        searchJob?.cancel()
        
        // Si no hay nada que buscar, volvemos a Idle
        if (query.length < 3 && genre == null && ageRange == null && !isIllustrated) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            try {
                // Construimos la query para la API
                var fullQuery = if (query.isEmpty()) "libros" else query
                
                genre?.let {
                    fullQuery += " subject:\"$it\""
                }

                // Obtenemos resultados base
                val results = repository.searchBooks(fullQuery)
                
                // Aplicamos filtrado manual para los campos que la API no filtra bien por query
                var filteredResults = results

                // Filtro de Edad (Simulado mapeando a targetAudience del modelo domain/Book)
                ageRange?.let { range ->
                    filteredResults = filteredResults.filter { book ->
                        when (range) {
                            "Adulto" -> book.targetAudience.contains("Adultos", ignoreCase = true)
                            "Juvenil" -> book.targetAudience.contains("Juvenil", ignoreCase = true)
                            "Prejuvenil" -> book.targetAudience.contains("Público General", ignoreCase = true)
                            else -> true
                        }
                    }
                }

                // Filtro de libros ilustrados
                if (isIllustrated) {
                    filteredResults = filteredResults.filter { it.isIllustrated }
                }
                
                if (filteredResults.isEmpty()) {
                    _uiState.value = SearchUiState.Error("No se encontraron pergaminos con esos criterios.")
                } else {
                    _uiState.value = SearchUiState.Success(filteredResults)
                }
            } catch (_: Exception) {
                _uiState.value = SearchUiState.Error("Error al consultar el catálogo remoto.")
            }
        }
    }
}
