package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Duration(
        @SerializedName("text") var text: String,
        @SerializedName("value") var value: Long
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeLong(value)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        var CREATOR = object : Parcelable.Creator<Duration> {
            override fun createFromParcel(parcel: Parcel) = Duration(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Duration?>(size)
        }
    }

}