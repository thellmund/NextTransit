package com.hellmund.transport.ui.destinations

import android.graphics.PorterDuff
import android.location.Location
import android.view.View
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.model.TripResult
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.util.plusAssign
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.list_item_destination.view.*

class DestinationViewHolder(
        itemView: View,
        private val presenter: DestinationsPresenter
) : RecyclerView.ViewHolder(itemView) {

    private val compositeDisposable = CompositeDisposable()

    fun bind(
            destination: Destination,
            location: Location?,
            listener: DestinationsAdapter.InteractionListener
    ) = with(itemView) {
        nameTextView.text = destination.title
        setOnClickListener { listener.onClick(destination) }
        optionsButton.setOnClickListener { listener.onOptionsSelected(destination) }

        if (destination.tripResult is TripResult.Loading) {
            refresh(destination, location)
        }

        render(destination.tripResult)
    }

    private fun refresh(destination: Destination, location: Location?) {
        if (location == null) {
            return
        }

        compositeDisposable += presenter
                .fetchTrip(destination, location)
                .doOnSuccess { destination.tripResult = it }
                .subscribe { render(it) }
    }

    private fun render(tripResult: TripResult) = with(itemView) {
        val isLoading = tripResult is TripResult.Loading
        val isSuccess = tripResult is TripResult.Success
        val isNone = tripResult is TripResult.None

        colorAccentView.visibility = if (isSuccess) VISIBLE else INVISIBLE
        durationUntilDepartureContainer.visibility = if (isSuccess) VISIBLE else INVISIBLE
        transitLineTextView.visibility = if (isLoading) INVISIBLE else if (isNone) GONE else VISIBLE

        errorTextView.visibility = if (isNone) VISIBLE else GONE
        loadingProgressBar.visibility = if (isLoading) VISIBLE else GONE

        if (tripResult is TripResult.Success) {
            renderTripDetails(tripResult.trip)
        }
    }

    private fun renderTripDetails(trip: Trip) = with(itemView) {
        durationUntilDepartureTextView.text = trip.formattedMinutesUntilDeparture
        unitTextView.text = trip.unitsText
        transitLineTextView.text = trip.formattedTransitLineInfo
        colorAccentView.background.setColorFilter(
                ContextCompat.getColor(context, trip.durationColor),
                PorterDuff.Mode.SRC_ATOP
        )
    }

    fun onDetached() {
        compositeDisposable.dispose()
    }

}

