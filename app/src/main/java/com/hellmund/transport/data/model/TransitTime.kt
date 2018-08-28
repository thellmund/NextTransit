package com.hellmund.transport.data.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hellmund.transport.R
import com.hellmund.transport.util.withinNextHour
import java.util.*

data class TransitTime(
        @SerializedName("text") var text: String,
        @SerializedName("value") var value: Long
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong()
    )

    val withinNextHour: Boolean
        get() = Date(value).withinNextHour

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeLong(value)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        var CREATOR = object : Parcelable.Creator<TransitTime> {
            override fun createFromParcel(parcel: Parcel) = TransitTime(parcel)

            override fun newArray(size: Int) = arrayOfNulls<TransitTime?>(size)
        }
    }

}