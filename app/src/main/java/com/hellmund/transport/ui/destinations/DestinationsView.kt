package com.hellmund.transport.ui.destinations

import com.hellmund.transport.data.model.Trip

interface DestinationsView {

    fun showLoading()
    fun showResult(trip: Trip)
    fun showError()

}