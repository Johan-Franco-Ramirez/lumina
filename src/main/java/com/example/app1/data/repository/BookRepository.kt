package com.example.app1.data.repository

import com.example.app1.data.api.OpenLibraryService
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
 */
class BookRepository(
    private val apiService: OpenLibraryService,
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
            )
        )
    }

    suspend fun getTrendingBooks(): List<Book> {
        return try {
            val response = apiService.searchBooks("trending", limit = 15)
            response.docs.map { it.toDomain() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getBookById(id: String): Book? {
        return try {
            val cleanId = id.removePrefix("OPEN_")
            val work = apiService.getWorkDetail(cleanId)
            val coverUrl = work.covers?.firstOrNull()?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
            
            Book(
                id = "OPEN_$cleanId",
                title = work.title,
                author = "Consultando autor...", 
                description = work.getDescriptionText(),
                coverUrl = coverUrl,
                genres = listOf("General"),
                targetAudience = "Público General",
                ageRange = "No especificada",
                isIllustrated = false,
                rating = null,
                origin = BookOrigin.REMOTE,
                readUrl = "https://openlibrary.org${work.key}"
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun searchBooks(query: String): List<Book> {
        return try {
            val response = apiService.searchBooks(query, limit = 15)
            response.docs.map { it.toDomain() }
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

    suspend fun getLocalBookById(bookId: String): Book? {
        return libraryDao.getBookById(bookId)?.toDomain()
    }

    fun getLibraryBooks(status: ReadingStatus): Flow<List<Book>> {
        return libraryDao.getBooksByStatus(status).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun savePersonalBook(book: Book) {
        libraryDao.insertBook(book.toEntity())
        libraryDao.updateLibraryStatus(
            LibraryBookEntity(bookId = book.id, status = ReadingStatus.WANT_TO_READ)
        )
    }

    suspend fun deleteBookFromLibrary(bookId: String) {
        libraryDao.deleteLibraryEntry(bookId)
        libraryDao.deleteBookEntry(bookId)
    }
}
