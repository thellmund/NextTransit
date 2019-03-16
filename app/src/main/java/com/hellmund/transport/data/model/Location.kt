package com.hellmund.transport.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
        @SerializedName("lat") var latitude: Double,
        @SerializedName("lng") var longitude: Double
) : Parcelable {

    fun toCoordinates() = Coordinates(latitude, longitude)

}
