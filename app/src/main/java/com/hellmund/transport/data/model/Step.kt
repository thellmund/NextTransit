package com.hellmund.transport.data.model

import android.content.Context
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hellmund.transport.R
import com.hellmund.transport.ui.route.TransitStepItem
import com.hellmund.transport.ui.route.WalkingStepItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Step(
        @SerializedName("travel_mode") var travelMode: String,
        @SerializedName("distance") var distance: Distance,
        @SerializedName("duration") var duration: Duration,
        @SerializedName("start_location") var startLocation: Location,
        @SerializedName("end_location") var endLocation: Location,
        @SerializedName("html_instructions") var instructions: String,
        @SerializedName("transit_details") var transitDetails: TransitDetails?
) : Parcelable {

    val isTransitStep: Boolean
        get() = travelMode == "TRANSIT"

    val routeIcon: Int
        get() = when (travelMode) {
            "WALKING" -> R.drawable.ic_directions_walk_black_48dp
            "TRANSIT" -> R.drawable.ic_outline_directions_transit_24px
            "BICYCLING" -> R.drawable.ic_outline_directions_bike_24px
            else -> R.drawable.ic_outline_directions_car_24px
        }

    fun toStepItem() = if (isTransitStep) {
        TransitStepItem(this)
    } else {
        WalkingStepItem(this)
    }

    fun getTransitDetails(context: Context): String {
        val headSign = transitDetails?.headSign
        val arrivalStop = transitDetails?.arrivalStop?.name
        return context.getString(R.string.route_transit_details, headSign, arrivalStop)
    }

}
