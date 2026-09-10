package com.example.app1.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * SETTINGS VIEWMODEL
 * 
 * ¿Qué es?
 * El gestor de las preferencias globales de la aplicación.
 * 
 * ¿Para qué sirve?
 * Controla el tema (Claro/Oscuro) de forma que el cambio se aplique 
 * en todas las pantallas al mismo tiempo.
 */
class SettingsViewModel : ViewModel() {
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}
