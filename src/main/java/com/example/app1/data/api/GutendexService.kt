package com.example.app1.data.api

import com.example.app1.data.model.GutendexBook
import com.example.app1.data.model.GutendexResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * INTERFAZ DE SERVICIO (GutendexService)
 *
 * Define los endpoints para interactuar con la API de Project Gutenberg.
 */
interface GutendexService {

    /**
     * Obtiene libros filtrados por idioma.
     * Por defecto busca libros en español ("es").
     */
    @GET("books/")
    suspend fun getSpanishBooks(
        @Query("languages") language: String = "es",
        @Query("page") page: Int = 1
    ): GutendexResponse

    /**
     * Busca libros por título o nombre de autor, opcionalmente filtrando por idioma.
     */
    @GET("books/")
    suspend fun searchBooks(
        @Query("search") query: String,
        @Query("languages") languages: String? = null,
        @Query("page") page: Int = 1
    ): GutendexResponse

    /**
     * Obtiene un libro específico por su ID.
     */
    @GET("books/{id}/")
    suspend fun getBookById(
        @Path("id") id: Int
    ): GutendexBook
}
