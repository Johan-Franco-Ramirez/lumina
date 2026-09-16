package com.example.app1.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DATA ACCESS OBJECT (LibraryDao)
 */
@Dao
interface LibraryDao {

    // --- Gestión de Libros ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): BookEntity?

    // --- Gestión de la Biblioteca (Favoritos/Estado) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLibraryStatus(libraryBook: LibraryBookEntity)

    @Query("SELECT * FROM library WHERE bookId = :bookId")
    suspend fun getLibraryEntry(bookId: String): LibraryBookEntity?

    @Delete
    suspend fun removeFromLibrary(libraryBook: LibraryBookEntity)

    @Query("DELETE FROM library WHERE bookId = :bookId")
    suspend fun deleteLibraryEntry(bookId: String)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookEntry(bookId: String)

    // --- Gestión de Caché (Carga Instantánea) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedBooks(books: List<CachedBookEntity>)

    @Query("SELECT * FROM cached_books WHERE category = :category ORDER BY timestamp DESC")
    suspend fun getCachedBooksByCategory(category: String): List<CachedBookEntity>

    @Query("DELETE FROM cached_books WHERE category = :category")
    suspend fun clearCacheByCategory(category: String)

    // --- Consultas Reactivas ---

    @Query("SELECT * FROM library")
    fun getAllLibraryEntries(): Flow<List<LibraryBookEntity>>

    @Query("""
        SELECT * FROM books 
        INNER JOIN library ON books.id = library.bookId 
        WHERE library.status = :status
    """)
    fun getBooksByStatus(status: ReadingStatus): Flow<List<BookEntity>>
}
