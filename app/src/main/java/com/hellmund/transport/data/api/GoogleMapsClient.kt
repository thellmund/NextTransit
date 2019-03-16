package com.hellmund.transport.data.api

import android.location.Location
import com.hellmund.transport.data.model.TransitStop

object GoogleMapsClient {

    private const val WALK_DIRECTIONS_URL = "http://maps.google.com/maps?mode=walking&saddr=%s&daddr=%s"

    fun routeToDepartureStopUrl(location: Location, transitStop: TransitStop): String {
        val fromCoordinates = "${location.latitude},${location.longitude}"
        val toCoordinates = transitStop.location.toCoordinates().toString()
        return String.format(GoogleMapsClient.WALK_DIRECTIONS_URL, fromCoordinates, toCoordinates)
    }

}
