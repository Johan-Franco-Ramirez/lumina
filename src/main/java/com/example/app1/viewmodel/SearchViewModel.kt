package com.example.app1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.app1.data.api.OpenLibraryService
import com.example.app1.data.api.GutendexClient
import com.example.app1.data.paging.CombinedBooksPagingSource
import com.example.app1.domain.model.Book
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.time.Duration.Companion.milliseconds

/**
 * SEARCH VIEWMODEL
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val openLibraryService = OpenLibraryService.create()
    private val gutendexService = GutendexClient.service
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow("")
    private val _selectedAgeRange = MutableStateFlow<String?>(null)
    private val _isIllustrated = MutableStateFlow(value = false)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagingDataFlow: Flow<PagingData<Book>> = _searchQuery
        .debounce(600.milliseconds) // Esperamos 600ms antes de disparar la búsqueda
        .flatMapLatest { query ->
            val genre = _selectedFilter.value
            val age = _selectedAgeRange.value
            val illustrated = _isIllustrated.value
            
            if (query.length < 3 && genre.isEmpty()) {
                MutableStateFlow(PagingData.empty())
            } else {
                searchBooksPaginated(query, genre, age, illustrated)
            }
        }.cachedIn(viewModelScope)

    private fun searchBooksPaginated(
        query: String, 
        genre: String, 
        age: String?, 
        illustrated: Boolean
    ): Flow<PagingData<Book>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = { 
            val combinedFilter = buildString {
                if (genre.isNotEmpty()) append("$genre ")
                age?.let { append("$it ") }
                if (illustrated) append("illustrated")
            }.trim()
            
            CombinedBooksPagingSource(openLibraryService, gutendexService, query, combinedFilter) 
        }
    ).flow

    /**
     * Activa la búsqueda con los filtros aplicados
     */
    fun performSearch(query: String, genre: String?, age: String?, illustrated: Boolean) {
        _selectedFilter.value = genre ?: ""
        _selectedAgeRange.value = age
        _isIllustrated.value = illustrated
        _searchQuery.value = query
    }
}
