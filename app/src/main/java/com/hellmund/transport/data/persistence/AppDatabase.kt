package com.hellmund.transport.data.persistence

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [(Destination::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun destinationsDao(): DestinationsDao

    companion object {

        private var database: AppDatabase? = null

        @Synchronized
        fun get(context: Context): AppDatabase {
            if (database == null) {
                database = Room
                        .databaseBuilder(context, AppDatabase::class.java, "db")
                        .build()
            }

            return database!!
        }

    }

}