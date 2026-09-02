package com.example.app1.data.repository

import com.example.app1.data.api.GoogleBooksService
import com.example.app1.data.database.LibraryBookEntity
import com.example.app1.data.database.LibraryDao
import com.example.app1.data.database.ReadingStatus
import com.example.app1.data.database.toDomain
import com.example.app1.data.database.toEntity
import com.example.app1.domain.model.Book
import com.example.app1.domain.model.BookOrigin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * REPOSITORIO DE LIBROS (BookRepository)
 * 
 * ¿Qué es?
 * El intermediario único para obtener datos de libros.
 * 
 * ¿Para qué sirve?
 * Oculta la complejidad de la API y de Room al resto de la app.
 */
class BookRepository(
    private val apiService: GoogleBooksService,
    private val libraryDao: LibraryDao,
) {

    // --- FUENTE DE DATOS: API (REMOTO) ---

    fun getRecommendedBooks(): List<Book> {
        return listOf(
            Book(
                id = "static_1",
                title = "Cien años de soledad",
                author = "Gabriel García Márquez",
                description = "En el mítico pueblo de Macondo, la estirpe de los Buendía está condenada a un siglo de aislamiento. Una epopeya donde lo fantástico se entrelaza con lo cotidiano, revelando los secretos de una familia que lucha contra el olvido en un laberinto de pasiones, guerras y profecías gitanas.",
                coverUrl = "https://images-na.ssl-images-amazon.com/images/I/71W1VpL3o0L.jpg",
                genres = listOf("Realismo Mágico", "Fiction"),
                targetAudience = "Adultos",
                ageRange = "18+",
                isIllustrated = false,
                rating = 4.9,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+cien+años+de+soledad+pdf"
            ),
            Book(
                id = "static_4",
                title = "El Principito",
                author = "Antoine de Saint-Exupéry",
                description = "Un piloto perdido en el Sahara encuentra a un pequeño príncipe de otro planeta. A través de su mirada pura, redescubrimos el valor de la amistad, la responsabilidad de lo que 'domesticamos' y la verdad invisible a los ojos en esta joya ilustrada de la literatura universal.",
                coverUrl = "https://m.media-amazon.com/images/I/71s+x-cgdYL.jpg",
                genres = listOf("Infantil", "Philosophy", "Fiction"),
                targetAudience = "Público General",
                ageRange = "Todas las edades",
                isIllustrated = true,
                rating = 4.9,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+el+principito+pdf"
            ),
            Book(
                id = "static_5",
                title = "Alicia en el país de las maravillas",
                author = "Lewis Carroll",
                description = "Sigue a Alicia por la madriguera del Conejo Blanco hacia un mundo donde la lógica se rompe y los sueños cobran vida. Un viaje surrealista lleno de naipes que hablan, gatos que desaparecen y una Reina de Corazones obsesionada con las ejecuciones.",
                coverUrl = "https://m.media-amazon.com/images/I/91t75bS7uTL.jpg",
                genres = listOf("Fantasía", "Fiction"),
                targetAudience = "Público General",
                ageRange = "9+",
                isIllustrated = true,
                rating = 4.7,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+alicia+en+el+pais+de+las+maravillas+pdf"
            ),
            Book(
                id = "static_6",
                title = "Sapiens: De animales a dioses",
                author = "Yuval Noah Harari",
                description = "¿Cómo logró una especie insignificante dominar el planeta? Un viaje fascinante por la historia de la humanidad, desde las cuevas hasta los algoritmos, cuestionando todo lo que creemos saber sobre nuestra cultura, religión y el futuro de nuestra especie.",
                coverUrl = "https://m.media-amazon.com/images/I/71cFLTvxKuL.jpg",
                genres = listOf("History", "Non-fiction"),
                targetAudience = "Adultos",
                ageRange = "16+",
                isIllustrated = false,
                rating = 4.8,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+sapiens+harari+pdf"
            ),
            Book(
                id = "static_7",
                title = "Breve historia del tiempo",
                author = "Stephen Hawking",
                description = "Desde el Big Bang hasta los agujeros negros, Hawking nos guía por los confines del universo. Un relato asombroso que explica los misterios del espacio y el tiempo con una claridad que desafía la complejidad del cosmos.",
                coverUrl = "https://m.media-amazon.com/images/I/912y30g2L5L.jpg",
                genres = listOf("Science", "Divulgación"),
                targetAudience = "Adultos",
                ageRange = "15+",
                isIllustrated = false,
                rating = 4.8,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+breve+historia+del+tiempo+pdf"
            ),
            Book(
                id = "static_8",
                title = "Meditaciones",
                author = "Marco Aurelio",
                description = "El diario íntimo de un emperador romano enfrentado a la guerra y la peste. Estas reflexiones estoicas ofrecen una guía atemporal para mantener la paz mental, la virtud y la fortaleza ante las adversidades de la vida.",
                coverUrl = "https://m.media-amazon.com/images/I/71d4xL9f3XL.jpg",
                genres = listOf("Philosophy", "Clásico"),
                targetAudience = "Adultos",
                ageRange = "18+",
                isIllustrated = false,
                rating = 4.9,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+meditaciones+marco+aurelio+pdf"
            ),
            Book(
                id = "static_9",
                title = "El sabueso de los Baskerville",
                author = "Arthur Conan Doyle",
                description = "En los sombríos páramos de Dartmoor, una maldición ancestral acecha a una familia noble. Sherlock Holmes deberá descifrar si el gigantesco perro negro que aúlla en la niebla es una bestia demoníaca o el plan maestro de un asesino humano.",
                coverUrl = "https://m.media-amazon.com/images/I/71uK5G9Sg-L.jpg",
                genres = listOf("Mystery", "Fiction"),
                targetAudience = "Jóvenes y Adultos",
                ageRange = "12+",
                isIllustrated = false,
                rating = 4.6,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+el+sabueso+de+los+baskerville+pdf"
            ),
            Book(
                id = "static_10",
                title = "La historia del arte",
                author = "E.H. Gombrich",
                description = "La introducción al arte más famosa del mundo. Desde las pinturas rupestres hasta la arquitectura moderna, Gombrich narra la historia del genio humano con una pasión que transforma cada cuadro y escultura en una ventana a la historia.",
                coverUrl = "https://m.media-amazon.com/images/I/81xG-w-X24L.jpg",
                genres = listOf("Art", "History"),
                targetAudience = "Público General",
                ageRange = "14+",
                isIllustrated = true,
                rating = 4.9,
                origin = BookOrigin.REMOTE,
                readUrl = "https://www.google.com/search?q=leer+la+historia+del+arte+gombrich+pdf"
            ),
            // Ejemplo de libro PDF Personal
            Book(
                id = "personal_1",
                title = "Mis Notas de Historia",
                author = "Yo (Personal)",
                description = "Un compendio personal con apuntes sobre la Revolución Industrial y sus efectos en la sociedad moderna. Incluye diagramas y cronologías propias.",
                coverUrl = null, // Usará un icono por defecto en la UI
                genres = listOf("History", "Personal"),
                targetAudience = "Personal",
                ageRange = "-",
                isIllustrated = false,
                rating = null,
                origin = BookOrigin.PERSONAL_PDF,
                pdfUri = "content://com.android.providers.downloads.documents/document/123" 
            )
        )
    }

    suspend fun getTrendingBooks(): List<Book> {
        return try {
            val response = apiService.searchBooks("subject:fiction", maxResults = 10)
            response.items?.map { dto ->
                Book(
                    id = dto.id,
                    title = dto.volumeInfo.title ?: "Sin título",
                    author = dto.volumeInfo.authors?.joinToString(", ") ?: "Autor desconocido",
                    description = dto.volumeInfo.description ?: "Sin descripción disponible.",
                    coverUrl = dto.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                    genres = dto.volumeInfo.categories ?: listOf("General"),
                    targetAudience = translateMaturity(dto.volumeInfo.maturityRating),
                    ageRange = "No especificada",
                    isIllustrated = dto.volumeInfo.description?.contains("illustrated", ignoreCase = true) ?: false,
                    rating = dto.volumeInfo.averageRating,
                    origin = BookOrigin.REMOTE,
                    readUrl = dto.volumeInfo.previewLink ?: dto.volumeInfo.infoLink
                )
            } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getBookById(id: String): Book? {
        return try {
            val dto = apiService.getBookById(id)
            Book(
                id = dto.id,
                title = dto.volumeInfo.title ?: "Sin título",
                author = dto.volumeInfo.authors?.joinToString(", ") ?: "Autor desconocido",
                description = dto.volumeInfo.description ?: "Sin descripción disponible.",
                coverUrl = dto.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                genres = dto.volumeInfo.categories ?: listOf("General"),
                targetAudience = translateMaturity(dto.volumeInfo.maturityRating),
                ageRange = "No especificada",
                isIllustrated = dto.volumeInfo.description?.contains("illustrated", ignoreCase = true) ?: false,
                rating = dto.volumeInfo.averageRating,
                origin = BookOrigin.REMOTE,
                readUrl = dto.volumeInfo.previewLink ?: dto.volumeInfo.infoLink
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun searchBooks(query: String): List<Book> {
        return try {
            val response = apiService.searchBooks(query)
            response.items?.map { dto ->
                Book(
                    id = dto.id,
                    title = dto.volumeInfo.title ?: "Sin título",
                    author = dto.volumeInfo.authors?.joinToString(", ") ?: "Autor desconocido",
                    description = dto.volumeInfo.description ?: "Sin descripción disponible.",
                    coverUrl = dto.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
                    genres = dto.volumeInfo.categories ?: emptyList(),
                    targetAudience = translateMaturity(dto.volumeInfo.maturityRating),
                    ageRange = "No especificada",
                    isIllustrated = dto.volumeInfo.description?.contains("illustrated", ignoreCase = true) ?: false,
                    rating = dto.volumeInfo.averageRating,
                    origin = BookOrigin.REMOTE,
                    readUrl = dto.volumeInfo.previewLink ?: dto.volumeInfo.infoLink
                )
            } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    // --- FUENTE DE DATOS: ROOM (LOCAL) ---

    suspend fun saveBookToLibrary(book: Book, status: ReadingStatus) {
        libraryDao.insertBook(book.toEntity())
        libraryDao.updateLibraryStatus(LibraryBookEntity(bookId = book.id, status = status))
    }

    suspend fun getBookLibraryStatus(bookId: String): ReadingStatus? {
        return libraryDao.getLibraryEntry(bookId)?.status
    }

    /**
     * Obtiene un libro de la base de datos local por su ID.
     */
    suspend fun getLocalBookById(bookId: String): Book? {
        return libraryDao.getBookById(bookId)?.toDomain()
    }

    fun getLibraryBooks(status: ReadingStatus): Flow<List<Book>> {
        return libraryDao.getBooksByStatus(status).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Guarda un libro personal (PDF) en la base de datos.
     */
    suspend fun savePersonalBook(book: Book) {
        // Guardamos los metadatos
        libraryDao.insertBook(book.toEntity())
        // Lo agregamos automáticamente a la biblioteca con estado 'POR LEER'
        libraryDao.updateLibraryStatus(
            LibraryBookEntity(bookId = book.id, status = ReadingStatus.WANT_TO_READ)
        )
    }

    /**
     * Elimina un libro de la biblioteca y sus metadatos.
     */
    suspend fun deleteBookFromLibrary(bookId: String) {
        libraryDao.deleteLibraryEntry(bookId)
        libraryDao.deleteBookEntry(bookId)
    }

    // --- UTILIDADES ---

    private fun translateMaturity(rating: String?): String {
        return when(rating) {
            "NOT_MATURE" -> "Público General"
            "MATURE" -> "Adultos"
            else -> "Público no especificado"
        }
    }
}
