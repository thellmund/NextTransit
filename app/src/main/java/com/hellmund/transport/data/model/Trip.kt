package com.hellmund.transport.data.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.hellmund.transport.R
import com.hellmund.transport.util.Constants

data class Trip(
        var departureStop: TransitStop,
        var departureVehicle: TransitVehicle,
        var departureTime: TransitTime,
        var minutesToDepartureStop: Int,
        var arrivalTime: TransitTime,
        var route: Route?
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(TransitStop::class.java.classLoader),
            parcel.readParcelable(TransitVehicle::class.java.classLoader),
            parcel.readParcelable(TransitTime::class.java.classLoader),
            parcel.readInt(),
            parcel.readParcelable(TransitTime::class.java.classLoader),
            parcel.readParcelable(Route::class.java.classLoader)
    )

    val hasRoute: Boolean
        get() = route != null

    val durationColor: Int
        get() {
            val buffer = departureTime.minutesUntil - minutesToDepartureStop
            return when {
                buffer > Constants.LONG_DURATION -> R.color.longDuration
                buffer > Constants.MEDIUM_DURATION -> R.color.mediumDuration
                else -> R.color.shortDuration
            }
        }

    fun getFormattedMinutesUntilDeparture(context: Context): String {
        return departureTime.getFormattedTimeUntil(context)
    }

    fun getUnitsText(context: Context): String {
        val minutes = departureTime.minutesUntil
        return if (minutes < 60) {
            context.getString(R.string.mins)
        } else {
            context.getString(R.string.hrs)
        }
    }

    fun getTransitLineInformation(context: Context) =
            String.format(context.getString(R.string.at_stop_name),
                    departureVehicle.name, departureStop.name)

    fun getCalendarBannerText(context: Context): String {
        val transitLine = getTransitLineInformation(context)
        return "$transitLine at ${departureTime.text}"
    }

    fun getNotificationText(context: Context): String {
        return String.format(context.getString(R.string.notification_text),
                departureTime.text, departureVehicle.name, departureStop.name)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(departureStop, flags)
        parcel.writeParcelable(departureVehicle, flags)
        parcel.writeParcelable(departureTime, flags)
        parcel.writeInt(minutesToDepartureStop)
        parcel.writeParcelable(arrivalTime, flags)
        parcel.writeParcelable(route, flags)
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<Trip> {
            override fun createFromParcel(parcel: Parcel) = Trip(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Trip?>(size)
        }

        fun fromResponse(response: MapsResponse): Trip? {
            val route = response.routes.firstOrNull() ?: return null
            val leg = route.legs.firstOrNull() ?: return null

            val departureStep = leg.getFirstTransitStep() ?: return null

            val departureTransitDetails = departureStep.transitDetails ?: return null

            val departureTime = departureTransitDetails.departureTime
            val durationToStop = leg.steps.first().duration.value
            val minutesToStop = Math.round(durationToStop.toFloat() / 60.0f)

            val arrivalTime = leg.arrivalTime
            val departureStop = departureTransitDetails.departureStop
            val line = departureTransitDetails.line.vehicle

            return Trip(departureStop, line, departureTime, minutesToStop, arrivalTime, route)
        }

    }

}