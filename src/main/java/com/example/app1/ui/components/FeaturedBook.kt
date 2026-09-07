package com.example.app1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.app1.domain.model.Book

/**
 * FEATURED BOOK (Libro Destacado)
 * 
 * ¿Qué es?
 * Un componente de gran tamaño diseñado para captar la atención del lector.
 * 
 * ¿Para qué sirve?
 * Resalta el libro más importante del día o una recomendación especial en 'Inicio'.
 * 
 * Estética:
 * Basado directamente en la referencia visual (Stitch). Usa el tema clásico
 * con una jerarquía clara y un botón de acción principal.
 */
@Composable
fun FeaturedBook(
    book: Book,
    onStartReading: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen Destacada
            AsyncImage(
                model = book.coverUrl,
                contentDescription = "Libro Destacado: ${book.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Géneros
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "GÉNEROS:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                book.genres.take(2).forEach { genre ->
                    GenreChip(genre)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Título y Autor
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Autor: ${book.author}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sinopsis
            Text(
                text = book.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Público / Recomendado (Sección destacada en la imagen)
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "RECOMMENDED FOR:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = book.targetAudience,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Acción Principal
            Button(
                onClick = onStartReading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Reading", fontWeight = FontWeight.Bold)
            }
        }
    }
}
