package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TransitStop(
        @SerializedName("location") var location: Location,
        @SerializedName("name") var name: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Location::class.java.classLoader),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(location, flags)
        parcel.writeString(name)
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<TransitStop> {
            override fun createFromParcel(parcel: Parcel) = TransitStop(parcel)

            override fun newArray(size: Int) = arrayOfNulls<TransitStop?>(size)
        }

    }

}