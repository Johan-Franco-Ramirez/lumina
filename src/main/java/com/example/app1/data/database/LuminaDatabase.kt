package com.example.app1.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * BASE DE DATOS PRINCIPAL (LuminaDatabase)
 */
@Database(
    entities = [BookEntity::class, LibraryBookEntity::class, CachedBookEntity::class], 
    version = 3,
    exportSchema = false
)
@TypeConverters(LuminaConverters::class)
abstract class LuminaDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var INSTANCE: LuminaDatabase? = null

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
