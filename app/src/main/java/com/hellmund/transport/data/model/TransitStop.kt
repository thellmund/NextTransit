package com.hellmund.transport.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransitStop(
        @SerializedName("location") var location: Location,
        @SerializedName("name") var name: String
) : Parcelable
