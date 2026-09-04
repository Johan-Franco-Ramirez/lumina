package com.example.app1.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app1.data.repository.ReaderRepository
import com.example.app1.domain.model.BookSource
import com.example.app1.domain.model.ReaderMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Representa todo lo que la interfaz gráfica necesita saber en tiempo real
data class ReaderUiState(
    val pages: List<Bitmap> = emptyList(),
    val isLoading: Boolean = false,
    val currentMode: ReaderMode = ReaderMode.ComicLTR, // Modo por defecto
    val currentPageIndex: Int = 0,
    val errorMessage: String? = null
)

class ReaderViewModel(
    private val repository: ReaderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    fun loadBook(source: BookSource, initialMode: ReaderMode) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentMode = initialMode) }

            repository.loadBookPages(source).collect { loadedPages ->
                _uiState.update {
                    it.copy(
                        pages = loadedPages,
                        isLoading = false,
                        errorMessage = if (loadedPages.isEmpty()) "No se pudieron cargar las páginas" else null
                    )
                }
            }
        }
    }

    fun changeReaderMode(newMode: ReaderMode) {
        _uiState.update { it.copy(currentMode = newMode) }
    }

    fun updateCurrentPage(index: Int) {
        _uiState.update { it.copy(currentPageIndex = index) }
        // Aquí conectarás en el futuro tu base de datos Room para guardar progreso de forma silenciosa
    }
}