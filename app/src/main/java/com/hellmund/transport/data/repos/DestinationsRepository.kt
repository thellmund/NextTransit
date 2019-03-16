package com.hellmund.transport.data.repos

import com.hellmund.transport.data.persistence.AppDatabase
import com.hellmund.transport.data.persistence.Destination
import io.reactivex.Observable
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class DestinationsRepository @Inject constructor(
        private val database: AppDatabase
) {

    val destinations: Observable<List<Destination>>
        get() = database.destinationsDao().getAll()

    fun getSize() = database.destinationsDao().getCount()

    fun insertDestination(destination: Destination) {
        database.destinationsDao().insert(destination)
    }

    fun updateDestination(vararg destinations: Destination) {
        database.destinationsDao().insert(*destinations)
    }

    fun updateDestinations(destinations: List<Destination>) {
        doAsync {
            updateDestination(*destinations.toTypedArray())
        }
    }

    fun removeDestination(destination: Destination) {
        doAsync {
            database.destinationsDao().delete(destination)
        }
    }

}
