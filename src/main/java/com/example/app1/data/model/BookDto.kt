package com.example.app1.data.model

import com.google.gson.annotations.SerializedName

/**
 * DATA TRANSFER OBJECTS (DTOs)
 * 
 * ¿Qué son?
 * Son clases que representan la estructura exacta del JSON que nos envía Google.
 * 
 * ¿Para qué sirven?
 * Retrofit usa estas clases para convertir automáticamente el texto (JSON) 
 * que viene de internet en objetos que Kotlin pueda entender.
 * 
 * ¿Por qué usamos @SerializedName?
 * Porque a veces los nombres en la API (en inglés o con guiones) no coinciden 
 * con nuestros nombres de variables en Kotlin.
 */

data class GoogleBooksResponse(
    @SerializedName("items") val items: List<BookItemDto>?,
)

data class BookItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfoDto
)

data class VolumeInfoDto(
    @SerializedName("title") val title: String?,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("categories") val categories: List<String>?,
    @SerializedName("imageLinks") val imageLinks: ImageLinksDto?,
    @SerializedName("averageRating") val averageRating: Double?,
    @SerializedName("maturityRating") val maturityRating: String?,
    @SerializedName("previewLink") val previewLink: String?,
    @SerializedName("infoLink") val infoLink: String?
)

data class ImageLinksDto(
    @SerializedName("thumbnail") val thumbnail: String?
)
