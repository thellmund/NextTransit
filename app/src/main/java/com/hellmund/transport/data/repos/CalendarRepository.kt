package com.hellmund.transport.data.repos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.hellmund.transport.data.model.CalendarEvent
import com.hellmund.transport.util.NonNullLiveData
import me.everything.providers.android.calendar.CalendarProvider
import org.jetbrains.anko.doAsync

class CalendarRepository(private val context: Context) {

    val hasDismissedPromo: Boolean
        get() {
            return ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED
        }

    val hasPermission: Boolean
        get() {
            return ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        }

    val nextEvent: NonNullLiveData<CalendarEvent>
        get () {
            val data = NonNullLiveData<CalendarEvent>()
            doAsync {
                val event = fetchNextEvent()
                data.postValue(event)
            }
            return data
        }

    private fun fetchNextEvent(): CalendarEvent? {
        val provider = CalendarProvider(context)
        val results = ArrayList<CalendarEvent>()

        for (calendar in provider.calendars.list) {
            val cursor = provider.getEvents(calendar.id).cursor
            while (cursor.moveToNext()) {
                val event = CalendarEvent.fromCursor(cursor) ?: continue
                results.add(event)
            }
        }

        return results
                .filter { it.isFutureEvent }
                .sorted()
                .firstOrNull()
    }

}