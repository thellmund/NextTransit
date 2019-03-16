package com.hellmund.transport.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(Destination::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun destinationsDao(): DestinationsDao

}
