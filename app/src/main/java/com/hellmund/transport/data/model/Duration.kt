package com.hellmund.transport.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Duration(
        @SerializedName("text") var text: String,
        @SerializedName("value") var value: Long
) : Parcelable
