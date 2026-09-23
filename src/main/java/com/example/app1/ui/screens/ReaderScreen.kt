package com.example.app1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.app1.domain.model.ReaderMode
import com.example.app1.viewmodel.ReaderViewModel
import com.example.app1.viewmodel.ReaderVisualTheme

@Preview
@Composable
fun ReaderScreenPreview() {
    Surface(color = Color.Black) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Vista Previa del Lector", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var isZoomed by remember { mutableStateOf(false) }
    
    // Configuración de Colores según el Tema Visual
    val backgroundColor = when (uiState.visualTheme) {
        ReaderVisualTheme.LIGHT -> Color.White
        ReaderVisualTheme.DARK -> Color.Black
        ReaderVisualTheme.SEPIA -> Color(0xFFF4ECD8)
    }

    val contentColor = when (uiState.visualTheme) {
        ReaderVisualTheme.LIGHT -> Color.Black
        ReaderVisualTheme.DARK -> Color.White
        ReaderVisualTheme.SEPIA -> Color(0xFF5B4636)
    }

    val colorFilter = when (uiState.visualTheme) {
        ReaderVisualTheme.SEPIA -> {
            val sepiaMatrix = ColorMatrix(floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f,     0f,     0f,     1f, 0f
            ))
            ColorFilter.colorMatrix(sepiaMatrix)
        }
        ReaderVisualTheme.DARK -> {
            val invertMatrix = ColorMatrix(floatArrayOf(
                -1f,  0f,  0f, 0f, 255f,
                 0f, -1f,  0f, 0f, 255f,
                 0f,  0f, -1f, 0f, 255f,
                 0f,  0f,  0f, 1f, 0f
            ))
            ColorFilter.colorMatrix(invertMatrix)
        }
        else -> null
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lector") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.changeVisualTheme(ReaderVisualTheme.LIGHT) }) {
                        Icon(Icons.Default.LightMode, contentDescription = "Claro", tint = if (uiState.visualTheme == ReaderVisualTheme.LIGHT) MaterialTheme.colorScheme.primary else contentColor)
                    }
                    IconButton(onClick = { viewModel.changeVisualTheme(ReaderVisualTheme.SEPIA) }) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Papiro", tint = if (uiState.visualTheme == ReaderVisualTheme.SEPIA) MaterialTheme.colorScheme.primary else contentColor)
                    }
                    IconButton(onClick = { viewModel.changeVisualTheme(ReaderVisualTheme.DARK) }) {
                        Icon(Icons.Default.DarkMode, contentDescription = "Noche", tint = if (uiState.visualTheme == ReaderVisualTheme.DARK) MaterialTheme.colorScheme.primary else contentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor.copy(alpha = 0.9f),
                    titleContentColor = contentColor,
                    navigationIconContentColor = contentColor
                )
            )
        },
        containerColor = backgroundColor
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
                    initialPage = uiState.currentPageIndex
                ) { uiState.pages.size }

                LaunchedEffect(pagerState.currentPage) {
                    viewModel.updateCurrentPage(pagerState.currentPage)
                    isZoomed = false // Reset zoom state when changing page
                }

                when (uiState.currentMode) {
                    ReaderMode.Webtoon -> {
                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            key = { it },
                            userScrollEnabled = !isZoomed
                        ) { pageIndex ->
                            ZoomableImage(
                                bitmap = uiState.pages[pageIndex],
                                contentDescription = "Página ${pageIndex + 1}",
                                contentScale = ContentScale.FillWidth,
                                colorFilter = colorFilter,
                                onZoomChange = { isZoomed = it }
                            )
                        }
                    }
                    else -> {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            key = { it },
                            reverseLayout = uiState.currentMode == ReaderMode.MangaRTL,
                            userScrollEnabled = !isZoomed
                        ) { pageIndex ->
                            ZoomableImage(
                                bitmap = uiState.pages[pageIndex],
                                contentDescription = "Página ${pageIndex + 1}",
                                contentScale = ContentScale.Fit,
                                colorFilter = colorFilter,
                                onZoomChange = { isZoomed = it }
                            )
                        }
                    }
                }
            } else if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage ?: "Error desconocido", color = contentColor, modifier = Modifier.padding(16.dp))
            } else {
                Text(text = "No hay páginas cargadas", color = contentColor)
            }
        }
    }
}

@Composable
fun ZoomableImage(
    bitmap: android.graphics.Bitmap,
    contentDescription: String,
    contentScale: ContentScale,
    colorFilter: ColorFilter?,
    onZoomChange: (Boolean) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 4f)
                    if (scale > 1f) {
                        offset += pan
                        onZoomChange(true)
                    } else {
                        offset = Offset.Zero
                        onZoomChange(false)
                    }
                }
            }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = contentScale,
            colorFilter = colorFilter
        )
    }
}
