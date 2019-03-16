package com.hellmund.transport.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Route(
        @SerializedName("legs") val legs: ArrayList<Leg>
) : Parcelable
