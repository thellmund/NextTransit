package com.hellmund.transport.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransitDetails(
        @SerializedName("arrival_stop") var arrivalStop: TransitStop,
        @SerializedName("arrival_time") var arrivalTime: TransitTime,
        @SerializedName("departure_stop") var departureStop: TransitStop,
        @SerializedName("departure_time") var departureTime: TransitTime,
        @SerializedName("headsign") var headSign: String,
        @SerializedName("line") var line: TransitLine
) : Parcelable
