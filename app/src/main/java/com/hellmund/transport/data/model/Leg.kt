package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Leg(
        @SerializedName("arrival_time") var arrivalTime: TransitTime,
        @SerializedName("departure_time") var departureTime: TransitTime,
        @SerializedName("distance") var distance: Distance,
        @SerializedName("duration") var duration: Duration,
        @SerializedName("start_address") var startAddress: String,
        @SerializedName("start_location") var startLocation: Location,
        @SerializedName("end_address") var endAddress: String,
        @SerializedName("end_location") var endLocation: Location,
        @SerializedName("steps") var steps: ArrayList<Step>
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(TransitTime::class.java.classLoader),
            parcel.readParcelable(TransitTime::class.java.classLoader),
            parcel.readParcelable(Distance::class.java.classLoader),
            parcel.readParcelable(Duration::class.java.classLoader),
            parcel.readString(),
            parcel.readParcelable(Location::class.java.classLoader),
            parcel.readString(),
            parcel.readParcelable(Location::class.java.classLoader),
            arrayListOf<Step>().apply {
                parcel.readList(this, Step::class.java.classLoader)
            }
    )

    fun getFirstTransitStep() = getFirstTransitStep(steps)

    private fun getFirstTransitStep(steps: List<Step>) = steps.firstOrNull { it.isTransitStep }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(arrivalTime, flags)
        parcel.writeParcelable(departureTime, flags)
        parcel.writeParcelable(distance, flags)
        parcel.writeParcelable(duration, flags)
        parcel.writeString(startAddress)
        parcel.writeParcelable(startLocation, flags)
        parcel.writeString(endAddress)
        parcel.writeParcelable(endLocation, flags)
        parcel.writeList(steps)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField var CREATOR = object : Parcelable.Creator<Leg> {
            override fun createFromParcel(parcel: Parcel) = Leg(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Leg?>(size)
        }
    }

}