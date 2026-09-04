package com.example.app1.data.repository

import android.graphics.Bitmap
import com.example.app1.domain.model.BookSource
import kotlinx.coroutines.flow.Flow

interface ReaderRepository {
    // Retorna un flujo (Flow) con la lista de páginas procesadas para manejo asíncrono
    fun loadBookPages(source: BookSource): Flow<List<Bitmap>>
}