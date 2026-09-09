package com.example.app1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app1.data.repository.ReaderRepositoryImpl

class ReaderViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReaderViewModel(ReaderRepositoryImpl(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
