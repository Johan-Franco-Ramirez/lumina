package com.example.app1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app1.ui.components.BookCard
import com.example.app1.viewmodel.SearchUiState
import com.example.app1.viewmodel.SearchViewModel

/**
 * PANTALLA DE BÚSQUEDA (SearchScreen)
 *
 * Conecta la UI con SearchViewModel para realizar búsquedas reales
 * en Google Books y Gutendex.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBookClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Estado local de los filtros
    var query by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var selectedAgeRange by remember { mutableStateOf<String?>(null) }
    var isIllustrated by remember { mutableStateOf(false) }

    val genres = listOf(
        "Fantasía", "Ciencia ficción", "Aventura", "Misterio",
        "Terror", "Romance", "Drama", "Historia", "Educación", "Infantil"
    )
    val ageRanges = listOf("Prejuvenil", "Juvenil", "Adulto")

    // Función para ejecutar la búsqueda y cerrar el teclado
    val executeSearch = {
        viewModel.performSearch(query, selectedGenre, selectedAgeRange, isIllustrated)
        keyboardController?.hide()
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- BARRA DE BÚSQUEDA ---
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { 
                        query = it
                        // Búsqueda dinámica con debouncing
                        viewModel.onSearchQueryChanged(it, selectedGenre ?: "")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Título, autor o palabra clave...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    // Configuración del teclado para que aparezca la lupa (Search)
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { executeSearch() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            // --- SECCIÓN DE FILTROS ---
            item {
                Text(
                    text = "Categoría: Género",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(genres) { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { 
                                selectedGenre = if (selectedGenre == genre) null else genre 
                                // Actualizamos búsqueda al cambiar el filtro
                                viewModel.performSearch(query, selectedGenre, selectedAgeRange, isIllustrated)
                            },
                            label = { Text(genre) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Rango de edad",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ageRanges) { range ->
                        FilterChip(
                            selected = selectedAgeRange == range,
                            onClick = { 
                                selectedAgeRange = if (selectedAgeRange == range) null else range 
                                viewModel.performSearch(query, selectedGenre, selectedAgeRange, isIllustrated)
                            },
                            label = { Text(range) }
                        )
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isIllustrated,
                        onCheckedChange = { 
                            isIllustrated = it 
                            viewModel.performSearch(query, selectedGenre, selectedAgeRange, isIllustrated)
                        }
                    )
                    Text("Solo libros ilustrados", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // --- BOTÓN APLICAR ---
            item {
                Button(
                    onClick = { executeSearch() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.FilterList, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("APLICAR FILTROS Y BUSCAR")
                }
            }

            // --- RESULTADOS ---
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            when (val state = uiState) {
                is SearchUiState.Idle -> {
                    item {
                        SearchInfoMessage(
                            title = "Busca tu próximo libro",
                            message = "Utiliza la barra de búsqueda y los filtros para explorar nuestra biblioteca."
                        )
                    }
                }
                is SearchUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is SearchUiState.Error -> {
                    item {
                        SearchInfoMessage(title = "Aviso", message = state.message)
                    }
                }
                is SearchUiState.Success -> {
                    // Mostramos resultados de 2 en 2 para una cuadrícula limpia
                    val chunks = state.results.chunked(2)
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
                            if (rowBooks.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchInfoMessage(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 32.dp),
            textAlign = TextAlign.Center
        )
    }
}
