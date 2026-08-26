package com.example.app1.domain.model

/**
 * MODELO DE DATOS CENTRAL - Book.kt
 * 
 * ¿Qué es?
 * Es una 'data class' que representa la entidad Libro en nuestra aplicación.
 * 
 * ¿Para qué sirve?
 * Es el objeto principal que viaja por toda la aplicación, desde la base de datos 
 * o la API hasta la pantalla del usuario.
 * 
 * ¿Por qué en la capa 'domain'?
 * Porque es un modelo "limpio". No depende de librerías externas (como Room o Retrofit).
 * Esto hace que nuestra lógica de negocio sea independiente de la tecnología.
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String?,
    val genres: List<String>,
    val targetAudience: String, // Ejemplo: "Infantil", "Juvenil", "Adultos"
    val ageRange: String,      // Ejemplo: "9-12", "18+"
    val isIllustrated: Boolean,
    val rating: Double?,
    val origin: BookOrigin = BookOrigin.REMOTE,
    val pdfUri: String? = null // Solo se llena si origin es PERSONAL_PDF
)

/**
 * ORIGEN DEL LIBRO
 * 
 * ¿Qué es?
 * Un enumerado que define de dónde proviene la información.
 * 
 * Relación:
 * Permite que la UI muestre etiquetas diferentes (Catálogo vs Personal).
 */
enum class BookOrigin {
    REMOTE,       // Proviene de Google Books API
    PERSONAL_PDF  // Subido por el usuario
}
