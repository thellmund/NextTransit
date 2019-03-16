package com.hellmund.transport.data.api

import com.hellmund.transport.data.model.MapsResponse
import com.hellmund.transport.data.model.SuggestionsResponse
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsAPI {

    @GET("api/directions/json")
    fun fetchTrip(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("arrival_time") arrivalTime: Long? = null,
            @Query("mode") mode: String = "transit"
    ): Maybe<MapsResponse>

    @GET("api/place/queryautocomplete/json")
    fun queryAddresses(
            @Query("input") input: String,
            @Query("types") types: String = "geocode"
    ): Maybe<SuggestionsResponse>

}