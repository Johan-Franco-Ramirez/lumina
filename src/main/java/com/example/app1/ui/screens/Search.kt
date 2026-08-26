package com.example.app1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app1.domain.model.Book
import com.example.app1.ui.components.BookCard
import com.example.app1.viewmodel.SearchUiState
import com.example.app1.viewmodel.SearchViewModel

/**
 * PANTALLA DE BÚSQUEDA (SearchScreen)
 * 
 * ¿Qué es?
 * El motor de exploración de LUMINA.
 * 
 * ¿Para qué sirve?
 * Permite buscar libros reales en Google Books mediante texto o categorías.
 * 
 * Conceptos:
 * 1. Debouncing: Implementado en el ViewModel para no saturar la API.
 * 2. FilterChips: Para filtrar rápidamente por géneros populares.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBookClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("") }

    val genres = listOf("Fiction", "History", "Philosophy", "Art", "Science", "Mystery")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorar Catálogo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // --- BARRA DE BÚSQUEDA ---
            OutlinedTextField(
                value = query,
                onValueChange = { 
                    query = it
                    viewModel.onSearchQueryChanged(it, selectedGenre)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Título, autor o palabra clave...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- FILTROS DE GÉNERO ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(genres) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = {
                            selectedGenre = if (selectedGenre == genre) "" else genre
                            viewModel.onSearchQueryChanged(query, selectedGenre)
                        },
                        label = { Text(genre) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- RESULTADOS ---
            when (val state = uiState) {
                is SearchUiState.Idle -> {
                    SearchInfoMessage(
                        title = "Biblioteca de Alejandría",
                        message = "Escribe al menos 3 letras para comenzar la búsqueda en el catálogo global."
                    )
                }
                is SearchUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is SearchUiState.Error -> {
                    SearchInfoMessage(
                        title = "Aviso",
                        message = state.message
                    )
                }
                is SearchUiState.Success -> {
                    SearchResultsList(
                        books = state.results,
                        onBookClick = onBookClick
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultsList(books: List<Book>, onBookClick: (String) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Mostramos resultados de 2 en 2 para mantener el estilo visual
        val chunks = books.chunked(2)
        items(chunks) { rowBooks ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowBooks.forEach { book ->
                    BookCard(
                        book = book,
                        modifier = Modifier.weight(1f),
                        onClick = { onBookClick(book.id) }
                    )
                }
                // Si la fila tiene solo un libro, añadimos un spacer para que no se estire
                if (rowBooks.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SearchInfoMessage(title: String, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
