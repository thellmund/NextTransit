package com.hellmund.transport.ui.destinations

import com.hellmund.transport.data.model.CalendarEvent
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.persistence.Destination

data class MainViewState(
        val isLoading: Boolean = false,
        val destinations: List<Destination> = emptyList(),
        val showCalendarPromo: Boolean = false,
        val nextEvent: Pair<CalendarEvent, Trip>? = null,
        val isLocationError: Boolean = false,
        val showNotifySnackbar: Boolean = false
) {

    companion object {
        fun initial() = MainViewState(isLoading = true)
    }

}
