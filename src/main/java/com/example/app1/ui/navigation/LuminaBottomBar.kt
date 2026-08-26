package com.example.app1.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * BARRA DE NAVEGACIÓN (LuminaBottomBar.kt)
 * 
 * ¿Qué es?
 * Un componente de la interfaz que muestra los botones de navegación en la parte inferior.
 * 
 * ¿Para qué sirve?
 * Permite al usuario cambiar de pantalla (sección) de forma rápida y visual.
 * 
 * ¿Cómo funciona?
 * 1. Obtiene la ruta actual desde el NavController.
 * 2. Itera sobre la lista 'navScreens' que definimos en Screen.kt.
 * 3. Marca como "seleccionado" el botón que coincide con la pantalla actual.
 */
@Composable
fun LuminaBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        navScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    // Evitamos navegar a la misma pantalla si ya estamos en ella
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Configuramos la navegación para no llenar la pila de pantallas
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}
