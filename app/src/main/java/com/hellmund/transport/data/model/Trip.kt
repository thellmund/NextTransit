package com.hellmund.transport.data.model

import android.content.Context
import android.os.Parcelable
import com.hellmund.transport.R
import com.hellmund.transport.util.Constants
import kotlinx.android.parcel.Parcelize

sealed class TripResult {
    data class Success(val trip: Trip) : TripResult()
    object None : TripResult()
    object Loading : TripResult()
}

@Parcelize
data class Trip(
        var departureStop: TransitStop,
        var departureVehicle: TransitVehicle,
        var departureTime: TransitTime,
        var minutesToDepartureStop: Int,
        val formattedMinutesUntilDeparture: String,
        val unitsText: String,
        val formattedTransitLineInfo: String,
        val calendarBannerText: String,
        val notificationText: String,
        var arrivalTime: TransitTime,
        var route: Route?
) : Parcelable {

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

    companion object {

        fun fromResponse(context: Context, response: MapsResponse): Trip? {
            val route = response.routes.firstOrNull() ?: return null
            val leg = route.legs.firstOrNull() ?: return null
            val departureStep = leg.firstTransitStep ?: return null
            val departureTransitDetails = departureStep.transitDetails ?: return null

            val departureTime = departureTransitDetails.departureTime
            val durationToStop = leg.steps.first().duration.value
            val minutesToStop = Math.round(durationToStop.toFloat() / 60.0f)
            val arrivalTime = leg.arrivalTime
            val departureStop = departureTransitDetails.departureStop
            val line = departureTransitDetails.line.vehicle

            val formattedMinutesUntilDeparture = departureTime.getFormattedTimeUntil(context)
            val unitsText = if (departureTime.minutesUntil < 60) {
                context.getString(R.string.mins)
            } else {
                context.getString(R.string.hrs)
            }

            val notificationText = context.getString(R.string.notification_text,
                    departureTime.text, line.name, departureStop.name)
            val transitLineInfo = context.getString(R.string.at_stop_name, line.name, departureStop.name)
            val calendarBannerText = context.getString(R.string.at_stop_name, transitLineInfo, departureTime.text)

            return Trip(departureStop, line, departureTime, minutesToStop,
                    formattedMinutesUntilDeparture, unitsText, transitLineInfo,
                    calendarBannerText, notificationText, arrivalTime, route)
        }

    }

}