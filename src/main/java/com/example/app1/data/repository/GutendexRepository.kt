package com.example.app1.data.repository

import com.example.app1.data.api.GutendexService
import com.example.app1.data.model.GutendexBook
import com.example.app1.data.paging.GutendexPagingSource
import com.example.app1.domain.model.Book
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * REPOSITORIO (GutendexRepository)
 *
 * Encapsula la lógica de obtención de datos desde GutendexService.
 */
class GutendexRepository(private val service: GutendexService) {

    /**
     * Obtiene una lista de libros en español mapeados al dominio.
     */
    suspend fun fetchSpanishBooks(): List<Book> = withContext(Dispatchers.IO) {
        try {
            val response = service.getSpanishBooks()
            response.results.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Busca libros por una palabra clave mapeados al dominio.
     * Permite filtrar por idioma (ej: "es").
     */
    suspend fun searchBooks(query: String, languages: String? = null): List<Book> = withContext(Dispatchers.IO) {
        try {
            val response = service.searchBooks(query, languages)
            response.results.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Obtiene un libro por su ID de Gutendex.
     */
    suspend fun getBookById(id: Int): Book? = withContext(Dispatchers.IO) {
        try {
            service.getBookById(id).toDomain()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Búsqueda paginada en Gutendex
     */
    fun searchBooksPaging(query: String, languages: String? = null): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(pageSize = 32, enablePlaceholders = false),
            pagingSourceFactory = { GutendexPagingSource(service, query, languages) }
        ).flow
    }
}
