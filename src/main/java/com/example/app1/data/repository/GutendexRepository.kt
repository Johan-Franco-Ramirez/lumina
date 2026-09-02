package com.example.app1.data.repository

import com.example.app1.data.api.GutendexService
import com.example.app1.data.model.GutendexBook
import com.example.app1.domain.model.Book
import kotlinx.coroutines.Dispatchers
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
}
