package com.hellmund.transport.ui.destinations

import com.hellmund.transport.data.persistence.Destination

interface OnDestinationsInteractionListener {

    fun onClick(destination: Destination)
    fun onOptionsSelected(destination: Destination)
    fun onDeleteSelected(destination: Destination, position: Int)
    fun onOrderChanged(destinations: List<Destination>)

}
