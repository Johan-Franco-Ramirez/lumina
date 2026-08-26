package com.example.app1.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * TEMA "BIBLIOTECA NOCTURNA" (Modo Oscuro)
 * Inspirado en pastas de libros antiguos de cuero y grabados en oro.
 */
private val DarkColorScheme = darkColorScheme(
    primary = BurnishedGold,
    onPrimary = Color.Black,
    secondary = AgedPaperText,
    onSecondary = DarkOak,
    background = DarkOak,
    surface = DeepMahogany,
    onBackground = AgedPaperText,
    onSurface = AgedPaperText,
)

/**
 * TEMA "EL ATRIO" (Modo Claro)
 * Inspirado en pergaminos y luz solar filtrada.
 */
private val LightColorScheme = lightColorScheme(
    primary = WoodBrown,
    onPrimary = Color.White,
    secondary = SepiaTinta,
    onSecondary = OldPaper,
    background = OldPaper,
    surface = SoftCream,
    onBackground = SepiaTinta,
    onSurface = SepiaTinta
)

@Composable
fun App1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        // Configuramos la barra de estado para que combine con el tema
        window.statusBarColor = colorScheme.background.value.toInt()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
