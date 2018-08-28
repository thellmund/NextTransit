package com.hellmund.transport.data.model

import android.database.Cursor
import android.provider.CalendarContract
import java.util.*

data class CalendarEvent(
        val title: String,
        val timestamp: Long,
        val location: String
) : Comparable<CalendarEvent> {

    val isFutureEvent: Boolean
        get() = Date(timestamp).after(Date())

    val getBannerTitle: String
        get() = "$title @ $location"

    override fun compareTo(other: CalendarEvent) = Date(timestamp).compareTo(Date(other.timestamp))

    companion object {

        fun fromCursor(cursor: Cursor): CalendarEvent? {
            val allDayIndex = cursor.getColumnIndex(CalendarContract.Events.ALL_DAY)
            val allDay = cursor.getInt(allDayIndex)
            if (allDay == 1) {
                return null
            }

            val titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE)
            val timeIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART)
            val locationIndex = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)

            val title = cursor.getString(titleIndex) ?: return null
            val start = cursor.getLong(timeIndex)
            val location = cursor.getString(locationIndex) ?: return null

            if (location.isBlank()) {
                return null
            }

            return CalendarEvent(title, start, location)
        }

    }

}