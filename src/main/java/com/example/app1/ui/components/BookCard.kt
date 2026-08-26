package com.example.app1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin

/**
 * BOOK CARD
 */
@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(160.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Portada con Badge de Origen
            Box {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = "Portada de ${book.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentScale = ContentScale.Crop
                )
                
                // Badge: [🌐 Catálogo] o [📄 Personal]
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (book.origin == BookOrigin.REMOTE) Icons.Default.Language else Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (book.origin == BookOrigin.REMOTE) "Catálogo" else "Personal",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (book.genres.isNotEmpty()) {
                    GenreChip(book.genres.first())
                }

                if (book.isIllustrated) {
                    Spacer(modifier = Modifier.height(4.dp))
                    IllustratedBadge()
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
