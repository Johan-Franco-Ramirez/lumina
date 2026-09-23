package com.example.app1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app1.data.api.GutendexService
import com.example.app1.domain.model.Book
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * FUENTE DE DATOS (CombinedBooksPagingSource)
 * 
 * Actualmente utiliza Gutendex (OpenLibrary desactivada).
 */
class CombinedBooksPagingSource(
    private val gutendexService: GutendexService,
    private val query: String,
    private val filter: String = ""
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 1

        // Si no hay consulta ni filtro, devolvemos página vacía inmediatamente
        if (query.isBlank() && filter.isBlank()) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        return try {
            val apiQuery = if (filter.isNotEmpty()) {
                if (query.isNotEmpty()) "$query $filter" else filter
            } else query

            val response = gutendexService.searchBooks(
                query = apiQuery,
                languages = "es",
                page = page
            )
            
            val books = response.results.map { it.toDomain() }

            LoadResult.Page(
                data = books,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (books.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
