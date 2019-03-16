package com.hellmund.transport.data.repos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.hellmund.transport.data.model.CalendarEvent
import io.reactivex.Maybe
import io.reactivex.Observable
import me.everything.providers.android.calendar.CalendarProvider
import javax.inject.Inject

class CalendarHelper @Inject constructor(
        private val context: Context
) {

    val events: Observable<Event> = Observable.fromCallable {
        if (hasDismissedPromo) Event.HAS_DISMISSED else Event.HAS_NOT_DISMISSED
    }

    private val hasDismissedPromo: Boolean
        get() {
            return ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED
        }

    val hasPermission: Boolean
        get() {
            return ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        }

    fun loadNextEvent(): Maybe<CalendarEvent> = Maybe.fromCallable(this::fetchNextEvent)

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

    enum class Event {
        HAS_NOT_DISMISSED, HAS_DISMISSED
    }

}