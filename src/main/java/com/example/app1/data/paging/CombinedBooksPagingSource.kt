package com.example.app1.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app1.data.api.OpenLibraryService
import com.example.app1.data.api.GutendexService
import com.example.app1.domain.model.Book
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * FUENTE DE DATOS COMBINADA (CombinedBooksPagingSource)
 * 
 * Mezcla resultados de OpenLibrary y Gutendex.
 * Implementa resiliencia: si una API falla, muestra los resultados de la otra.
 */
class CombinedBooksPagingSource(
    private val openLibraryService: OpenLibraryService,
    private val gutendexService: GutendexService,
    private val query: String,
    private val filter: String = ""
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 1
        val limit = params.loadSize / 2

        // Si no hay consulta ni filtro, devolvemos página vacía inmediatamente
        if (query.isBlank() && filter.isBlank()) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        return try {
            coroutineScope {
                // Ejecutamos ambas peticiones en paralelo
                val openLibraryDeferred = async {
                    try {
                        val apiQuery = if (filter.isNotEmpty()) {
                            if (query.isNotEmpty()) "$query $filter" else filter
                        } else query
                        
                        openLibraryService.searchBooks(
                            query = apiQuery,
                            page = page,
                            limit = limit
                        ).docs.map { it.toDomain() }
                    } catch (e: Exception) {
                        emptyList<Book>()
                    }
                }
                
                val gutendexDeferred = async {
                    try {
                        // Solo buscamos en Gutendex si hay una palabra clave (query)
                        if (query.isNotBlank()) {
                            gutendexService.searchBooks(
                                query = query,
                                languages = "es",
                                page = page
                            ).results.map { it.toDomain() }
                        } else {
                            emptyList<Book>()
                        }
                    } catch (e: Exception) {
                        emptyList<Book>()
                    }
                }

                // Esperamos los resultados
                val openLibraryBooks = openLibraryDeferred.await()
                val gutendexBooks = gutendexDeferred.await()

                // Combinamos y eliminamos duplicados por título
                val combined = (openLibraryBooks + gutendexBooks)
                    .distinctBy { it.title.lowercase().trim() }

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
