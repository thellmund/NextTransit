package com.hellmund.transport.data.api

import com.hellmund.transport.data.model.MapsResponse
import com.hellmund.transport.data.model.SuggestionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsAPI {

    @GET("api/directions/json")
    fun getTripToLocation(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("arrival_time") arrivalTime: Long? = null,
            @Query("mode") mode: String = "transit"
    ): Call<MapsResponse>

    @GET("api/place/queryautocomplete/json")
    fun queryAddresses(
            @Query("input") input: String,
            @Query("types") types: String = "geocode"
    ): Call<SuggestionsResponse>

}