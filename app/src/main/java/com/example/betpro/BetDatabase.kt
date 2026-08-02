package com.example.betpro

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(entities = [BetEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BetDatabase : RoomDatabase() {
    abstract fun betDao(): BetDao

    companion object {
        @Volatile
        private var INSTANCE: BetDatabase? = null

        fun getInstance(context: Context): BetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BetDatabase::class.java,
                    "bet_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}