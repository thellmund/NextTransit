package com.hellmund.transport.ui.destinations

import android.content.Context
import com.hellmund.transport.data.model.MapsResponse
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.model.TripResult
import io.reactivex.functions.Function
import javax.inject.Inject

class TripResultMapper @Inject constructor(
        private val context: Context
) : Function<MapsResponse, TripResult> {

    override fun apply(mapsResponse: MapsResponse): TripResult {
        val trip = Trip.fromResponse(context, mapsResponse)
        return trip?.let { TripResult.Success(it) } ?: TripResult.None
    }

}
