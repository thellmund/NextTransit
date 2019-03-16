package com.hellmund.transport.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Coordinates(
        val lat: Double,
        val lng: Double
) : Parcelable {

    override fun toString() = "$lat,$lng"

}
