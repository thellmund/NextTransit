package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TransitDetails(
        @SerializedName("arrival_stop") var arrivalStop: TransitStop,
        @SerializedName("arrival_time") var arrivalTime: TransitTime,
        @SerializedName("departure_stop") var departureStop: TransitStop,
        @SerializedName("departure_time") var departureTime: TransitTime,
        @SerializedName("headsign") var headSign: String,
        @SerializedName("line") var line: TransitLine) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(TransitStop::class.java.classLoader),
            parcel.readParcelable(TransitTime::class.java.classLoader),
            parcel.readParcelable(TransitStop::class.java.classLoader),
            parcel.readParcelable(TransitTime::class.java.classLoader),
            parcel.readString(),
            parcel.readParcelable(TransitLine::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeParcelable(arrivalStop, flags)
            writeParcelable(arrivalTime, flags)
            writeParcelable(departureStop, flags)
            writeParcelable(departureTime, flags)
            writeString(headSign)
            writeParcelable(line, flags)
        }
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<TransitDetails> {
            override fun createFromParcel(parcel: Parcel) = TransitDetails(parcel)

            override fun newArray(size: Int) = arrayOfNulls<TransitDetails?>(size)
        }

    }

}