package com.example.app1.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.app1.data.database.ReadingStatus
import com.example.app1.domain.model.BookOrigin
import com.example.app1.ui.components.AgeBadge
import com.example.app1.ui.components.GenreChip
import com.example.app1.ui.components.IllustratedBadge
import com.example.app1.viewmodel.BookDetailUiState
import com.example.app1.viewmodel.BookDetailViewModel

/**
 * PANTALLA DE DETALLE DEL LIBRO (BookDetailScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    onBack: () -> Unit,
    viewModel: BookDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Obra") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Share, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is BookDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is BookDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is BookDetailUiState.Success -> {
                val book = state.book
                val status = state.libraryStatus

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Portada Grande
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = book.coverUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(200.dp)
                                .height(300.dp)
                                .background(Color.LightGray.copy(alpha = 0.2f)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título y Autor
                    Text(text = book.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(text = "por ${book.author}", style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.secondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Etiquetas y Público
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        book.genres.take(3).forEach { genre -> GenreChip(genre) }
                        if (book.isIllustrated) IllustratedBadge()
                    }
                    AgeBadge(age = book.targetAudience)

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- BOTÓN: LEER (Gutendex o PDF) ---
                    val readingUrl = book.readUrl ?: book.pdfUri
                    if (readingUrl != null) {
                        Button(
                            onClick = {
                                // Al empezar a leer, lo movemos automáticamente a "Leyendo"
                                viewModel.updateReadingStatus(book, ReadingStatus.READING)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(readingUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (status == ReadingStatus.READING) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.tertiary
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(Icons.Default.AutoStories, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (status == ReadingStatus.READING) "CONTINUAR LEYENDO" else "COMENZAR LECTURA")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // --- BOTONES DE ACCIÓN (ROOM) ---
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Botón: Quiero leer
                        Button(
                            onClick = { viewModel.updateReadingStatus(book, ReadingStatus.WANT_TO_READ) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp),
                            colors = if (status == ReadingStatus.WANT_TO_READ) {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            } else {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            }
                        ) {
                            Icon(
                                if (status == ReadingStatus.WANT_TO_READ) Icons.Default.BookmarkAdded else Icons.Default.LibraryAdd, 
                                null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (status == ReadingStatus.WANT_TO_READ) "En Lista" else "Por leer")
                        }

                        // Botón: Leído
                        Button(
                            onClick = { viewModel.updateReadingStatus(book, ReadingStatus.READ) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp),
                            colors = if (status == ReadingStatus.READ) {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            } else {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            }
                        ) {
                            Icon(if (status == ReadingStatus.READ) Icons.Default.Check else Icons.Default.Check, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (status == ReadingStatus.READ) "Finalizado" else "Ya me lo leí")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sinopsis
                    Text(text = "Sinopsis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = book.description, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
