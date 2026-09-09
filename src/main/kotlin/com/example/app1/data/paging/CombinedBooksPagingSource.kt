package com.example.app1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app1.data.api.OpenLibraryService
import com.example.app1.data.api.GutendexService
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class CombinedBooksPagingSource(
    private val openLibraryService: OpenLibraryService,
    private val gutendexService: GutendexService,
    private val query: String,
    private val filter: String = ""
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 1
        val limit = params.loadSize / 2

        return try {
            coroutineScope {
                val openLibraryDeferred = async {
                    openLibraryService.searchBooks(
                        query = if (filter.isNotEmpty()) "$query $filter" else query,
                        page = page,
                        limit = limit
                    )
                }
                
                val gutendexDeferred = async {
                    gutendexService.searchBooks(
                        query = query,
                        languages = "es",
                        page = page
                    )
                }

                val openLibraryResponse = openLibraryDeferred.await()
                val gutendexResponse = gutendexDeferred.await()

                val openLibraryBooks = openLibraryResponse.docs.map { it.toDomain() }
                val gutendexBooks = gutendexResponse.results.map { it.toDomain() }

                val combined = (openLibraryBooks + gutendexBooks).distinctBy { it.title.lowercase() }

                LoadResult.Page(
                    data = combined,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (combined.isEmpty()) null else page + 1
                )
            }
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
