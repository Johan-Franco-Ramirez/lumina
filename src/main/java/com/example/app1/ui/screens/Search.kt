package com.example.app1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.app1.domain.model.Book
import com.example.app1.ui.components.BookCard
import com.example.app1.viewmodel.SearchViewModel

/**
 * PANTALLA DE BÚSQUEDA (SearchScreen)
 *
 * Actualizada con filtros por categorías: Género, Edad e Ilustraciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBookClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel(),
) {
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    
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
                    onValueChange = { query = it },
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
                            onClick = { selectedGenre = if (selectedGenre == genre) null else genre },
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
                            onClick = { selectedAgeRange = if (selectedAgeRange == range) null else range },
                            label = { Text(range) }
                        )
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isIllustrated,
                        onCheckedChange = { isIllustrated = it }
                    )
                    Text("Solo libros ilustrados", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // --- BOTÓN APLICAR ---
            item {
                Button(
                    onClick = { 
                        viewModel.performSearch(query, selectedGenre, selectedAgeRange, isIllustrated)
                    },
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
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            if (pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading) {
                item {
                    SearchInfoMessage(
                        title = "Busca tu próximo libro",
                        message = "Utiliza la barra de búsqueda y los filtros para explorar nuestra biblioteca."
                    )
                }
            } else {
                when (pagingItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is LoadState.Error -> {
                        item {
                            SearchInfoMessage(title = "Aviso", message = "Error al cargar resultados.")
                        }
                    }
                    else -> {
                        // Mostramos resultados de 2 en 2
                        val count = pagingItems.itemCount
                        for (i in 0 until count step 2) {
                            item(key = pagingItems[i]?.id ?: i) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    val item1 = pagingItems[i]
                                    val item2 = if (i + 1 < count) pagingItems[i + 1] else null

                                    if (item1 != null) {
                                        BookCard(
                                            book = item1,
                                            modifier = Modifier.weight(1f),
                                            onClick = { onBookClick(item1.id) }
                                        )
                                    }
                                    if (item2 != null) {
                                        BookCard(
                                            book = item2,
                                            modifier = Modifier.weight(1f),
                                            onClick = { onBookClick(item2.id) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Indicador de carga al final
                        if (pagingItems.loadState.append is LoadState.Loading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchInfoMessage(title: String, message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
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
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
