package com.example.app1.data.api

import com.example.app1.data.model.OpenLibrarySearchResponse
import com.example.app1.data.model.OpenLibraryWork
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * INTERFAZ DE SERVICIO (OpenLibraryService)
 * 
 * Reemplaza a Google Books API.
 */
interface OpenLibraryService {

    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponse

    @GET("works/{workId}.json")
    suspend fun getWorkDetail(
        @Path("workId") workId: String
    ): OpenLibraryWork

    companion object {
        private const val BASE_URL = "https://openlibrary.org/"

        fun create(): OpenLibraryService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenLibraryService::class.java)
        }
    }
}
