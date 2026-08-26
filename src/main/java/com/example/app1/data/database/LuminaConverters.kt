package com.example.app1.data.database

import androidx.room.TypeConverter
import com.example.app1.domain.model.BookOrigin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * CONVERTIDORES DE TIPO (TypeConverters)
 * 
 * ¿Qué son?
 * Room solo sabe guardar tipos básicos (números, texto). No sabe guardar listas 
 * u objetos complejos.
 * 
 * ¿Para qué sirven?
 * Convierten esos tipos complejos en algo que SQLite entienda (como un texto JSON) 
 * y viceversa.
 */
class LuminaConverters {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromBookOrigin(origin: BookOrigin): String {
        return origin.name
    }

    @TypeConverter
    fun toBookOrigin(value: String): BookOrigin {
        return BookOrigin.valueOf(value)
    }

    @TypeConverter
    fun fromReadingStatus(status: ReadingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toReadingStatus(value: String): ReadingStatus {
        return ReadingStatus.valueOf(value)
    }
}
