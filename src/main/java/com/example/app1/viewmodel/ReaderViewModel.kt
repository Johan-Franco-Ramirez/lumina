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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

enum class ReaderVisualTheme {
    LIGHT, DARK, SEPIA
}

data class ReaderUiState(
    val pages: List<Bitmap> = emptyList(),
    val isLoading: Boolean = false,
    val currentMode: ReaderMode = ReaderMode.ComicLTR,
    val visualTheme: ReaderVisualTheme = ReaderVisualTheme.SEPIA,
    val currentPageIndex: Int = 0,
    val errorMessage: String? = null
)

class ReaderViewModel(
    private val repository: ReaderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private val loadMutex = Mutex()

    fun loadBook(source: BookSource, initialMode: ReaderMode) {
        viewModelScope.launch {
            // Añadimos un retraso deliberado de 3 segundos para apreciar la carga del archivo
            delay(1500.milliseconds)

            // Evitamos que dos cargas se ejecuten al mismo tiempo
            loadMutex.withLock {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        currentMode = initialMode,
                        pages = emptyList(),
                        currentPageIndex = 0,
                        errorMessage = null
                    )
                }

                repository.loadBookPages(source).collect { loadedPages ->
                    _uiState.update {
                        it.copy(
                            pages = loadedPages,
                            isLoading = false,
                            errorMessage = if (loadedPages.isEmpty()) {
                                "No se encontraron imágenes compatibles. Si es un archivo .cbr, asegúrate de que sea formato ZIP y no RAR."
                            } else null
                        )
                    }
                }
            }
        }
    }

    fun changeReaderMode(newMode: ReaderMode) {
        _uiState.update { it.copy(currentMode = newMode) }
    }

    fun changeVisualTheme(newTheme: ReaderVisualTheme) {
        _uiState.update { it.copy(visualTheme = newTheme) }
    }

    fun updateCurrentPage(index: Int) {
        _uiState.update { it.copy(currentPageIndex = index) }
    }
}
