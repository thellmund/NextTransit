package com.hellmund.transport.data.model

import android.content.Context
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hellmund.transport.R
import com.hellmund.transport.util.isWithinNextHour
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TransitTime(
        @SerializedName("text") var text: String,
        @SerializedName("value") var value: Long
) : Parcelable {

    val withinNextHour: Boolean
        get() = Date(value).isWithinNextHour

    val minutesUntil: Int
        get() {
            val now = System.currentTimeMillis()
            val timestampMillis = value * 1000 // timestamp is in seconds
            val difference = timestampMillis - now
            val differenceSeconds = (difference / 1000).toInt()
            return differenceSeconds / 60
        }

    fun getFormattedTimeUntil(context: Context): String {
        val minutes = minutesUntil
        return if (minutes < 60) {
            minutes.toString()
        } else {
            val hours = (minutes / 60).toString()
            val mins = String.format("%02d", minutes % 60)
            String.format(context.getString(R.string.hours_to_departure), hours, mins)
        }
    }

}
