package com.example.app1.ui

import androidx.lifecycle.ViewModel
import com.example.app1.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Guest : AuthState()
    data class Authenticated(val user: User) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Guest)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(username: String, email: String) {
        // Simulación de login exitoso
        _authState.value = AuthState.Authenticated(User(username, email))
    }

    fun logout() {
        _authState.value = AuthState.Guest
    }

    fun updateFavorites(bookId: String) {
        val currentState = _authState.value
        if (currentState is AuthState.Authenticated) {
            val currentFavorites = currentState.user.favoriteBookIds
            val newFavorites = if (currentFavorites.contains(bookId)) {
                currentFavorites - bookId
            } else {
                currentFavorites + bookId
            }
            _authState.value = AuthState.Authenticated(
                currentState.user.copy(favoriteBookIds = newFavorites)
            )
        }
    }
}
