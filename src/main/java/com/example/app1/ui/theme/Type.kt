package com.example.app1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * TIPOGRAFÍA DE LUMINA
 * Utilizamos FontFamily.Serif para dar el estilo de "libro impreso".
 * Esto ayuda a la legibilidad y refuerza la identidad clásica de la app.
 */
val Typography = Typography(
    // Estilo para títulos grandes (Nombres de libros)
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    // Estilo para el cuerpo de texto (Descripciones, sinopsis)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Estilo para etiquetas técnicas (ISBN, Editorial)
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)
