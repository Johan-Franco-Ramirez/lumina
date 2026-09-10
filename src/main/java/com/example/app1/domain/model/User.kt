package com.example.app1.domain.model

/**
 * MODELO DE USUARIO (User.kt)
 * 
 * ¿Qué es?
 * Representa al usuario autenticado en la aplicación.
 */
data class User(
    val username: String,
    val email: String,
    val favoriteBookIds: List<String> = emptyList()
)
