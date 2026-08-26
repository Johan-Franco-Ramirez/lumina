package com.example.app1.data.api

import com.example.app1.data.model.BookItemDto
import com.example.app1.data.model.GoogleBooksResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * INTERFAZ DE SERVICIO (GoogleBooksService)
 * 
 * ¿Qué es?
 * Es el contrato que le dice a Retrofit cómo realizar las peticiones a la API.
 * 
 * ¿Para qué sirve?
 * Define las URL (endpoints) y los parámetros que necesitamos para obtener libros.
 */
interface GoogleBooksService {
    
    // Petición GET para buscar libros por una palabra clave (query)
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
    ): GoogleBooksResponse

    // Petición GET para obtener un libro específico por su identificador único
    @GET("volumes/{volumeId}")
    suspend fun getBookById(
        @Path("volumeId") volumeId: String
    ): BookItemDto

    // Objeto Singleton para acceder al servicio desde cualquier parte
    companion object {
        private const val BASE_URL = "https://www.googleapis.com/books/v1/"

        fun create(): GoogleBooksService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleBooksService::class.java)
        }
    }
}
