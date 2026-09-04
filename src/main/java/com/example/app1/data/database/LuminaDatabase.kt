package com.example.app1.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * BASE DE DATOS PRINCIPAL (LuminaDatabase)
 * 
 * ¿Qué es?
 * La pieza central que une las tablas (Entities) con las órdenes (DAO).
 * 
 * ¿Para qué sirve?
 * Administra la conexión con SQLite y asegura que solo exista una instancia 
 * abierta en toda la aplicación (Patrón Singleton).
 */
@Database(
    entities = [BookEntity::class, LibraryBookEntity::class], 
    version = 2,
    exportSchema = false
)
@TypeConverters(LuminaConverters::class)
abstract class LuminaDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: LuminaDatabase? = null

        /**
         * Singleton para obtener la base de datos.
         * Asegura que no se abran múltiples archivos de base de datos a la vez.
         */
        fun getDatabase(context: Context): LuminaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuminaDatabase::class.java,
                    "lumina_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
