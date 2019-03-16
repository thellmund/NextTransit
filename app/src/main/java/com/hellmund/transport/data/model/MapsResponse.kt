package com.hellmund.transport.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapsResponse(
        @SerializedName("routes") val routes: List<Route>
) : Parcelable
