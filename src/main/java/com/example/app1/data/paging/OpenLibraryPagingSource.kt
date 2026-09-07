package com.example.app1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app1.data.api.OpenLibraryService
import com.example.app1.domain.model.Book

class OpenLibraryPagingSource(
    private val apiService: OpenLibraryService,
    private val query: String
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 1
        return try {
            val response = apiService.searchBooks(
                query = query,
                page = page,
                limit = params.loadSize
            )
            val books = response.docs.map { it.toDomain() }

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
