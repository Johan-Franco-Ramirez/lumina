package com.example.app1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app1.data.api.GutendexService
import com.example.app1.domain.model.Book

class GutendexPagingSource(
    private val service: GutendexService,
    private val query: String,
    private val language: String? = null
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val position = params.key ?: 1
        return try {
            val response = if (query.isEmpty() && language != null) {
                service.getSpanishBooks(language, page = position)
            } else {
                service.searchBooks(query, languages = language, page = position)
            }
            
            val books = response.results.map { it.toDomain() }
            
            LoadResult.Page(
                data = books,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.next == null) null else position + 1
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
