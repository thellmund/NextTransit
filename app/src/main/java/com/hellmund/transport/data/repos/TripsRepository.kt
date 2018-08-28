package com.hellmund.transport.data.repos

import android.location.Location
import android.util.Log
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.model.MapsResponse
import com.hellmund.transport.data.api.GoogleMapsClient
import com.hellmund.transport.util.NonNullLiveData
import com.hellmund.transport.util.toCoordinates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripsRepository {

    fun getTrip(location: Location?, destination: String, timestamp: Long): NonNullLiveData<Trip> {
        val origin = location?.toCoordinates().toString()
        val data = NonNullLiveData<Trip>()

        val arrivalTime = timestamp / 1000 // The Google Maps API uses seconds since epoch

        GoogleMapsClient
                .mapsApi
                .getTripToLocation(origin, destination, arrivalTime)
                .enqueue(object : Callback<MapsResponse> {
                    override fun onResponse(call: Call<MapsResponse>?, response: Response<MapsResponse>?) {
                        val body = response?.body()
                        if (body != null) {
                            data.postValue(Trip.fromResponse(body))
                        } else {
                            data.postValue(null)
                        }
                    }

                    override fun onFailure(call: Call<MapsResponse>?, t: Throwable?) {
                        Log.d(logTag, "Error when loading trip", t)
                        data.postValue(null)
                    }
                })

        return data
    }

    companion object {
        private val logTag = TripsRepository::class.java.simpleName
    }

}
