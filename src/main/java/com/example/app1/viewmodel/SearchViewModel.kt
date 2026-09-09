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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    private data class SearchParameters(
        val query: String,
        val genre: String,
        val age: String?,
        val illustrated: Boolean
    )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagingDataFlow: Flow<PagingData<Book>> = combine(
        _searchQuery.debounce(600.milliseconds),
        _selectedFilter,
        _selectedAgeRange,
        _isIllustrated
    ) { query, genre, age, illustrated ->
        SearchParameters(query, genre, age, illustrated)
    }.flatMapLatest { params ->
        if (params.query.length < 3 && params.genre.isEmpty()) {
            flowOf(PagingData.empty())
        } else {
            searchBooksPaginated(params.query, params.genre, params.age, params.illustrated)
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
