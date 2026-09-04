package com.example.app1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app1.ui.navigation.LuminaBottomBar
import com.example.app1.ui.navigation.Screen
import com.example.app1.ui.screens.BookDetailScreen
import com.example.app1.ui.screens.HomeScreen
import com.example.app1.ui.screens.LibraryScreen
import com.example.app1.ui.screens.ProfileScreen
import com.example.app1.ui.screens.ReaderScreen
import com.example.app1.ui.screens.SearchScreen
import com.example.app1.ui.theme.App1Theme
import com.example.app1.viewmodel.ReaderViewModel
import com.example.app1.viewmodel.ReaderViewModelFactory
import com.example.app1.viewmodel.SettingsViewModel

/**
 * MAIN ACTIVITY - El punto de entrada de LUMINA
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Obtenemos el ViewModel de configuración para el tema
            val settingsViewModel: SettingsViewModel = viewModel()
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            App1Theme(darkTheme = isDarkTheme) {
                LuminaApp(settingsViewModel)
            }
        }
    }
}

/**
 * LUMINA APP - Orquestador de Navegación
 */
@Composable
fun LuminaApp(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    val context = LocalContext.current

    // Inicializamos el ReaderViewModel con su Factory
    val readerViewModel: ReaderViewModel = viewModel(
        factory = ReaderViewModelFactory(context)
    )

    Scaffold(
        bottomBar = { LuminaBottomBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { 
                HomeScreen(onBookClick = { bookId -> 
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                }) 
            }
            composable(Screen.Search.route) { 
                SearchScreen(onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                })
            }
            composable(Screen.Library.route) { 
                LibraryScreen(
                    onBookClick = { bookId -> 
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onNavigateToReader = {
                        navController.navigate(Screen.Reader.route)
                    },
                    readerViewModel = readerViewModel
                ) 
            }
            composable(Screen.Profile.route) { 
                ProfileScreen(
                    onToggleTheme = { settingsViewModel.toggleTheme() },
                    isDarkTheme = isDarkTheme
                ) 
            }
            
            composable(Screen.Reader.route) {
                ReaderScreen(
                    viewModel = readerViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookDetailScreen(
                    bookId = bookId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
