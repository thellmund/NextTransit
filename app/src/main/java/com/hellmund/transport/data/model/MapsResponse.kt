package com.hellmund.transport.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MapsResponse(
        @SerializedName("routes") val routes: List<Route>
) : Parcelable {

    constructor(parcel: Parcel) : this(
            listOf<Route>().apply {
                parcel.readList(this, Route::class.java.classLoader)
            }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(routes)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<MapsResponse> {
        override fun createFromParcel(parcel: Parcel): MapsResponse {
            return MapsResponse(parcel)
        }

        override fun newArray(size: Int): Array<MapsResponse?> {
            return arrayOfNulls(size)
        }
    }


}