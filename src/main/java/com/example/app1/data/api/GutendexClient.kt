package com.example.app1.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * CLIENTE API (GutendexClient)
 *
 * Proporciona una instancia Singleton de Retrofit configurada para Gutendex.
 */
object GutendexClient {
    private const val BASE_URL = "https://gutendex.com/"

    /**
     * Instancia perezosa del servicio.
     */
    val service: GutendexService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GutendexService::class.java)
    }
}
