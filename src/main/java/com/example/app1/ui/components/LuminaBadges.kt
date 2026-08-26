package com.example.app1.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * GENRE CHIP
 * 
 * ¿Qué es?
 * Una pequeña etiqueta con el nombre del género literario.
 * 
 * ¿Para qué sirve?
 * Ayuda al usuario a identificar rápidamente de qué trata el libro.
 */
@Composable
fun GenreChip(genre: String) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            shape = RoundedCornerShape(4.dp)
        )
    ) {
        Text(
            text = genre.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelLarge,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * AGE BADGE
 * 
 * ¿Qué es?
 * Una insignia que muestra el público o rango de edad.
 * 
 * ¿Para qué sirve?
 * Clasificación educativa para que padres o lectores sepan si el contenido es adecuado.
 */
@Composable
fun AgeBadge(age: String) {
    Text(
        text = "RECOMENDADO PARA: $age",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

/**
 * ILLUSTRATED BADGE
 * 
 * ¿Qué es?
 * Una etiqueta especial con un icono o estilo distintivo.
 * 
 * Relación:
 * Resuelve el requisito 12 del proyecto (etiqueta especial [🎨 ILUSTRADO]).
 */
@Composable
fun IllustratedBadge() {
    Surface(
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "🎨 ILUSTRADO",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelLarge,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
