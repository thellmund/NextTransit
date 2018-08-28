package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Route(
        @SerializedName("legs") val legs: ArrayList<Leg>
) : Parcelable {

    constructor(parcel: Parcel) : this(
            arrayListOf<Leg>().apply {
                parcel.readList(this, Leg::class.java.classLoader)
            })

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(legs)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField var CREATOR = object : Parcelable.Creator<Route> {
            override fun createFromParcel(parcel: Parcel) = Route(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Route?>(size)
        }
    }

}