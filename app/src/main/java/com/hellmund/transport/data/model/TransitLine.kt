package com.hellmund.transport.data.model

import android.graphics.Color
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransitLine(
        @SerializedName("color") var color: String?,
        @SerializedName("name") var name: String?,
        @SerializedName("short_name") var shortName: String?,
        @SerializedName("text_color") var textColor: String?,
        @SerializedName("vehicle") var vehicle: TransitVehicle
) : Parcelable {

    val hexColor: Int?
        get() = if (color != null) Color.parseColor(color) else null

    val shortNameOrName: String
        get() {
            shortName?.let { return it }
            name?.let { return it }
            return vehicle.name
        }

}
