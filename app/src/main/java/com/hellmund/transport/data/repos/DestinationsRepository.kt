package com.hellmund.transport.data.repos

import android.content.Context
import androidx.lifecycle.LiveData
import com.hellmund.transport.data.persistence.AppDatabase
import com.hellmund.transport.data.persistence.Destination
import org.jetbrains.anko.doAsync

class DestinationsRepository(context: Context) {

    private val database = AppDatabase.get(context)

    fun size() = database.destinationsDao().getCount()

    val destinations: LiveData<List<Destination>>
        get() = database.destinationsDao().getAll()

    fun insertDestination(destination: Destination) {
        database.destinationsDao().insert(destination)
    }

    fun updateDestination(destination: Destination) {
        database.destinationsDao().insert(destination)
    }

    fun updateDestinations(destinations: List<Destination>) {
        doAsync {
            destinations.forEach {  destination ->
                database.destinationsDao().update(destination)
            }
        }
    }

    fun removeDestination(destination: Destination) {
        doAsync {
            database.destinationsDao().delete(destination)
        }
    }

}