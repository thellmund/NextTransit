package com.hellmund.transport.data.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hellmund.transport.R

data class Step(
        @SerializedName("travel_mode") var travelMode: String,
        @SerializedName("distance") var distance: Distance,
        @SerializedName("duration") var duration: Duration,
        @SerializedName("start_location") var startLocation: Location,
        @SerializedName("end_location") var endLocation: Location,
        @SerializedName("html_instructions") var instructions: String,
        @SerializedName("transit_details") var transitDetails: TransitDetails?
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(Distance::class.java.classLoader),
            parcel.readParcelable(Duration::class.java.classLoader),
            parcel.readParcelable(Location::class.java.classLoader),
            parcel.readParcelable(Location::class.java.classLoader),
            parcel.readString(),
            parcel.readParcelable(TransitDetails::class.java.classLoader)
    )

    val isTransitStep: Boolean
        get() = travelMode == "TRANSIT"

    val stepItem: StepItem
        get() = if (isTransitStep) {
            TransitStepItem(this)
        } else {
            WalkingStepItem(this)
        }

    val routeIcon: Int
        get() = when (travelMode) {
            "WALKING" -> R.drawable.ic_directions_walk_black_48dp
            "TRANSIT" -> R.drawable.ic_directions_transit_black_48dp
            "BICYCLING" -> R.drawable.ic_directions_bike_black_48dp
            else -> R.drawable.ic_directions_car_black_48dp
        }

    fun getTransitDetails(context: Context): String {
        val headSign = transitDetails?.headSign
        val arrivalStop = transitDetails?.arrivalStop?.name
        return String.format(context.getString(R.string.route_transit_details), headSign, arrivalStop)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(travelMode)
        parcel.writeParcelable(distance, flags)
        parcel.writeParcelable(duration, flags)
        parcel.writeParcelable(startLocation, flags)
        parcel.writeParcelable(endLocation, flags)
        parcel.writeString(instructions)
        parcel.writeParcelable(transitDetails, flags)
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<Step> {
            override fun createFromParcel(parcel: Parcel) = Step(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Step?>(size)
        }

    }

}