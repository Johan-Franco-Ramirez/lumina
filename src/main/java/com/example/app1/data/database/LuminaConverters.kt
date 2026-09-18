package com.example.app1.data.database

import androidx.room.TypeConverter
import com.example.app1.domain.model.BookOrigin
import com.example.app1.domain.model.BookType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * CONVERTIDORES DE TIPO (TypeConverters)
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

    @TypeConverter
    fun fromBookType(type: BookType): String {
        return type.name
    }

    @TypeConverter
    fun toBookType(value: String): BookType {
        return try {
            BookType.valueOf(value)
        } catch (e: Exception) {
            BookType.LIBRO
        }
    }
}
