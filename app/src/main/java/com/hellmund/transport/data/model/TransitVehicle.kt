package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TransitVehicle(
        @SerializedName("name") val name: String
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<TransitVehicle> {
            override fun createFromParcel(parcel: Parcel) = TransitVehicle(parcel)

            override fun newArray(size: Int) = arrayOfNulls<TransitVehicle?>(size)
        }

    }

}