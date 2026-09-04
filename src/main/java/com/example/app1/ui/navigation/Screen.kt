package com.example.app1.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * RUTAS DE NAVEGACIÓN (Screen.kt)
 * 
 * ¿Qué es?
 * Es una clase sellada (sealed class) que define los destinos posibles en la app.
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Search : Screen("search", "Buscar", Icons.Default.Search)
    object Library : Screen("library", "Mi Biblioteca", Icons.AutoMirrored.Filled.LibraryBooks)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    
    // Ruta para el lector
    object Reader : Screen("reader", "Lector", Icons.AutoMirrored.Filled.MenuBook)

    // Ruta con argumento para el detalle del libro
    object BookDetail : Screen("bookDetail/{bookId}", "Detalle", Icons.AutoMirrored.Filled.MenuBook) {
        fun createRoute(bookId: String) = "bookDetail/$bookId"
    }
}

// Lista de pantallas para iterar en la barra de navegación (solo las principales)
val navScreens = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Library,
    Screen.Profile,
)
