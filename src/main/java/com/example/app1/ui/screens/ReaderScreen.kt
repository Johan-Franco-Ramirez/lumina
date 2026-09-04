package com.example.app1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.app1.domain.model.ReaderMode
import com.example.app1.viewmodel.ReaderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lector") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else if (uiState.pages.isNotEmpty()) {
                val pagerState = rememberPagerState(
                    initialPage = uiState.currentPageIndex,
                    pageCount = { uiState.pages.size }
                )

                // Sincronizar el estado del pager con el ViewModel (opcional, para guardar progreso)
                LaunchedEffect(pagerState.currentPage) {
                    viewModel.updateCurrentPage(pagerState.currentPage)
                }

                when (uiState.currentMode) {
                    ReaderMode.Webtoon -> {
                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            key = { it }
                        ) { pageIndex ->
                            Image(
                                bitmap = uiState.pages[pageIndex].asImageBitmap(),
                                contentDescription = "Página ${pageIndex + 1}",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }
                    else -> {
                        // PDF, MangaRTL, ComicLTR usan HorizontalPager por ahora
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            key = { it },
                            reverseLayout = uiState.currentMode == ReaderMode.MangaRTL
                        ) { pageIndex ->
                            Image(
                                bitmap = uiState.pages[pageIndex].asImageBitmap(),
                                contentDescription = "Página ${pageIndex + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "Error desconocido",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    text = "No hay páginas cargadas",
                    color = Color.White
                )
            }
        }
    }
}
