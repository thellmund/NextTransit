package com.hellmund.transport.ui.destinations

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.location.Location
import com.hellmund.transport.data.model.CalendarEvent
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.data.repos.DestinationsRepository
import com.hellmund.transport.data.repos.TripsRepository
import com.hellmund.transport.util.NonNullLiveData

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val destinationsRepo = DestinationsRepository(application.applicationContext)
    private val tripRepo = TripsRepository()

    var location: Location? = null

    val destinations: LiveData<List<Destination>>
        get() = destinationsRepo.destinations

    fun updateDestinations(destinations: List<Destination>) {
        destinationsRepo.updateDestinations(destinations)
    }

    fun removeDestination(destination: Destination) {
        destinationsRepo.removeDestination(destination)
    }

    fun getTripForEvent(event: CalendarEvent): NonNullLiveData<Trip> {
        return tripRepo.getTrip(location, event.location, event.timestamp)
    }

}
