package com.hellmund.transport.data.model

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TransitLine(
        @SerializedName("color") var color: String?,
        @SerializedName("name") var name: String?,
        @SerializedName("short_name") var shortName: String?,
        @SerializedName("text_color") var textColor: String?,
        @SerializedName("vehicle") var vehicle: TransitVehicle
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(TransitVehicle::class.java.classLoader)
    )

    val hexColor: Int?
        get() = if (color != null) Color.parseColor(color) else null

    val shortNameOrName: String
        get() {
            shortName?.let { return it }
            name?.let { return it }
            return vehicle.name
        }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(color)
        parcel.writeString(name)
        parcel.writeString(shortName)
        parcel.writeString(textColor)
        parcel.writeParcelable(vehicle, flags)
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<TransitLine> {
            override fun createFromParcel(parcel: Parcel) = TransitLine(parcel)

            override fun newArray(size: Int) = arrayOfNulls<TransitLine?>(size)
        }

    }

}