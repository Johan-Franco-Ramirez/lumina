package com.example.app1.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * CLIENTE API (GutendexClient)
 *
 * Proporciona una instancia Singleton de Retrofit configurada para Gutendex.
 */
object GutendexClient {
    private const val BASE_URL = "https://gutendex.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /**
     * Instancia perezosa del servicio.
     */
    val service: GutendexService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GutendexService::class.java)
    }
}
