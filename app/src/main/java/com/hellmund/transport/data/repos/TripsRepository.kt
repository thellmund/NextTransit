package com.hellmund.transport.data.repos

import android.location.Location
import com.hellmund.transport.data.api.GoogleMapsAPI
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.ui.destinations.TripMapper
import com.hellmund.transport.util.toCoordinates
import io.reactivex.Maybe
import javax.inject.Inject

class TripsRepository @Inject constructor(
        private val mapsApi: GoogleMapsAPI,
        private val tripMapper: TripMapper
) {

    fun fetchTrip(location: Location?, destination: String, timestamp: Long): Maybe<Trip> {
        val origin = location?.toCoordinates().toString()
        val arrivalTime = timestamp / 1000 // The Google Maps API uses seconds since epoch
        return mapsApi
                .fetchTrip(origin, destination, arrivalTime)
                .map(tripMapper)
    }

}
