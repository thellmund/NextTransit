package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Location(
        @SerializedName("lat") var latitude: Double,
        @SerializedName("lng") var longitude: Double
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readDouble(), parcel.readDouble())

    fun toCoordinates() = Coordinates(latitude, longitude)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeDouble(latitude)
            writeDouble(longitude)
        }
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<Location> {
            override fun createFromParcel(parcel: Parcel) = Location(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Location?>(size)
        }

    }

}