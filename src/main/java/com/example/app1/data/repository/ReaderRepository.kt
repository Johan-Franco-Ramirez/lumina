package com.example.app1.data.repository

import android.graphics.Bitmap
import com.example.app1.domain.model.BookSource
import kotlinx.coroutines.flow.Flow

interface ReaderRepository {
    fun loadBookPages(source: BookSource): Flow<List<Bitmap>>
}
