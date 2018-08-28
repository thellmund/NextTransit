package com.hellmund.transport.data.api

import android.location.Location
import com.hellmund.transport.data.model.TransitStop
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleMapsClient {

    private const val BASE_URL = "https://maps.googleapis.com/maps/"
    private const val WALK_DIRECTIONS_URL =
            "http://maps.google.com/maps?mode=walking&saddr=%s&daddr=%s"

    private val retrofit: Retrofit

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor(GoogleMapsInterceptor())
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val mapsApi: GoogleMapsAPI
        get() = retrofit.create(GoogleMapsAPI::class.java)

    fun routeToDepartureStopURL(location: Location, transitStop: TransitStop): String {
        val fromCoordinates = "${location.latitude},${location.longitude}"
        val toCoordinates = transitStop.location.toCoordinates().toString()
        return String.format(GoogleMapsClient.WALK_DIRECTIONS_URL, fromCoordinates, toCoordinates)
    }

}