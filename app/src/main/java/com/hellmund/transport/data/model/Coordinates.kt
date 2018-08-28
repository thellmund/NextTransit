package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable

data class Coordinates(
        val lat: Double,
        val lng: Double
) : Parcelable {

    private constructor(parcel: Parcel) : this(parcel.readDouble(), parcel.readDouble())

    override fun toString() = "$lat,$lng"

    override fun describeContents() = 0

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<Coordinates> = object : Parcelable.Creator<Coordinates> {
            override fun createFromParcel(parcel: Parcel): Coordinates {
                return Coordinates(parcel)
            }

            override fun newArray(size: Int): Array<Coordinates?> {
                return arrayOfNulls<Coordinates?>(size)
            }
        }

    }

}