package com.hellmund.transport.ui.destinations

import android.content.Context
import com.hellmund.transport.data.model.MapsResponse
import com.hellmund.transport.data.model.Trip
import io.reactivex.functions.Function
import javax.inject.Inject

class TripMapper @Inject constructor(
        private val context: Context
) : Function<MapsResponse, Trip?> {

    override fun apply(mapsResponse: MapsResponse): Trip? {
        return Trip.fromResponse(context, mapsResponse)
    }

}
