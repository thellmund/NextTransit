package com.hellmund.transport.ui.destinations

import android.content.Context
import android.location.Location
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.model.MapsResponse
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.data.api.GoogleMapsClient
import com.hellmund.transport.util.toCoordinates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DestinationsPresenter(val context: Context) {

    fun fetchTrip(view: DestinationsView, destination: Destination, location: Location?) {
        if (location == null) {
            // Didn't acquire the device location yet
            return
        }

        val origin = location.toCoordinates().toString()

        GoogleMapsClient
                .mapsApi
                .getTripToLocation(origin, destination.address)
                .enqueue(object : Callback<MapsResponse> {
                    override fun onResponse(call: Call<MapsResponse>,
                                            response: Response<MapsResponse>) {
                        val body = response.body()
                        handleResponse(view, destination, body)
                    }

                    override fun onFailure(call: Call<MapsResponse>, t: Throwable) {
                        view.showError()
                    }
                })
    }

    private fun handleResponse(view: DestinationsView,
                               destination: Destination, response: MapsResponse?) {
        if (response == null) {
            view.showError()
            return
        }

        val trip = Trip.fromResponse(response)
        trip?.let {
            destination.trip = it
            view.showResult(it)
            return
        }

        view.showError()
    }

}
