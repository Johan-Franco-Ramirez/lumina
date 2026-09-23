package com.example.app1.ui.screens

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookSource
import com.example.app1.domain.model.ReaderMode
import com.example.app1.ui.components.BookCard
import com.example.app1.ui.components.ImportBookDialog
import com.example.app1.ui.components.LuminaLoading
import com.example.app1.util.LuminaOrganizer
import com.example.app1.viewmodel.LibraryViewModel
import com.example.app1.viewmodel.ReaderViewModel
import java.io.File

/**
 * PANTALLA: MI BIBLIOTECA (LibraryScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBookClick: (String) -> Unit,
    onNavigateToReader: () -> Unit,
    viewModel: LibraryViewModel = viewModel(),
    readerViewModel: ReaderViewModel = viewModel(),
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("POR LEER", "LEYENDO", "LEÍDOS", "LOCAL")

    val wantToRead by viewModel.wantToReadBooks.collectAsState(initial = emptyList())
    val reading by viewModel.readingBooks.collectAsState(initial = emptyList())
    val read by viewModel.readBooks.collectAsState(initial = emptyList())

    // --- LÓGICA DE IMPORTACIÓN PDF ---
    var showImportDialog by remember { mutableStateOf(false) }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                selectedPdfUri = it
                showImportDialog = true
            }
        }
    )

    if (showImportDialog && selectedPdfUri != null) {
        ImportBookDialog(
            pdfUri = selectedPdfUri.toString(),
            onDismiss = { showImportDialog = false },
            onConfirm = { title, author, desc, readerType ->
                viewModel.importPersonalBook(title, author, desc, selectedPdfUri.toString(), readerType)
                showImportDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mi Biblioteca", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { 
                                Text(
                                    text = title, 
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ) 
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    filePickerLauncher.launch(arrayOf(
                        "application/pdf", 
                        "application/zip", 
                        "application/octet-stream",
                        "application/x-cbz",
                        "application/x-cbr",
                        "image/*"
                    )) 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Importar Archivo")
            }
        }
    ) { padding ->
        when (selectedTab) {
            3 -> {
                LocalReaderSection(
                    padding = padding,
                    readerViewModel = readerViewModel,
                    onNavigateToReader = onNavigateToReader
                )
            }
            else -> {
                val currentList = when (selectedTab) {
                    0 -> wantToRead
                    1 -> reading
                    else -> read
                }

                LibraryContent(
                    padding = padding,
                    books = currentList,
                    onBookClick = onBookClick,
                    onDeleteBook = { bookId -> viewModel.deleteBook(bookId) },
                    emptyMessage = when (selectedTab) {
                        0 -> "No tienes libros pendientes por leer."
                        1 -> "No estás leyendo ningún libro actualmente."
                        else -> "Aún no has marcado ningún libro como leído."
                    }
                )
            }
        }
    }
}

@Composable
fun LocalReaderSection(
    padding: PaddingValues,
    readerViewModel: ReaderViewModel,
    onNavigateToReader: () -> Unit
) {
    val context = LocalContext.current
    val organizer = remember { LuminaOrganizer(context) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let { safeUri ->
                val fileName = safeUri.toString().lowercase()
                val initialMode = when {
                    fileName.contains(".pdf") -> ReaderMode.PDF
                    fileName.contains(".cbz") || fileName.contains(".zip") || fileName.contains(".cbr") -> ReaderMode.ComicLTR
                    else -> ReaderMode.Webtoon
                }
                readerViewModel.loadBook(source = BookSource.Local(safeUri), initialMode = initialMode)
                onNavigateToReader()
            }
        },
    )

    // Lanzador para seleccionar CARPETAS completas para LECTURA
    val folderReaderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { uri: Uri? ->
            uri?.let { folderUri ->
                val rootDoc = DocumentFile.fromTreeUri(context, folderUri)
                val files = rootDoc?.listFiles()?.filter { doc ->
                    val name = doc.name?.lowercase() ?: ""
                    name.endsWith(".cbz") || name.endsWith(".zip") || name.endsWith(".cbr") || 
                    name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".webp")
                }?.map { it.uri }

                if (!files.isNullOrEmpty()) {
                    readerViewModel.loadBook(BookSource.Collection(files), ReaderMode.ComicLTR)
                    onNavigateToReader()
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Carga un archivo local para leerlo al instante.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { 
                    filePickerLauncher.launch(arrayOf(
                        "application/pdf", 
                        "application/zip", 
                        "application/octet-stream", // Para archivos sin tipo claro
                        "application/x-cbz",
                        "application/x-cbr",
                        "image/*" // Acepta todas las imágenes
                    ))
                }
            ) {
                Text("Subir y Leer Archivo Local")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { folderReaderLauncher.launch(null) }
            ) {
                Text("Leer Carpeta Completa (.cbr/.cbz)")
            }
        }
    }
}

@Composable
fun LibraryContent(
    padding: PaddingValues,
    books: List<Book>,
    onBookClick: (String) -> Unit,
    onDeleteBook: (String) -> Unit,
    emptyMessage: String
) {
    if (books.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                BookCard(
                    book = book,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onBookClick(book.id) },
                    onDeleteClick = { onDeleteBook(book.id) }
                )
            }
        }
    }
}
