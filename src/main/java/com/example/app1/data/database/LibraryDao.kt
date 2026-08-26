package com.example.app1.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DATA ACCESS OBJECT (LibraryDao)
 * 
 * ¿Qué es?
 * La interfaz que define cómo interactuamos con las tablas.
 * 
 * ¿Para qué sirve?
 * Aquí escribimos las consultas SQL. Room se encarga de ejecutarlas 
 * de forma segura y eficiente.
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
