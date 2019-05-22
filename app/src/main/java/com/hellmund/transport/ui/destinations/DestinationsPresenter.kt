package com.hellmund.transport.ui.destinations

import android.location.Location
import com.hellmund.transport.data.api.GoogleMapsAPI
import com.hellmund.transport.data.model.TripResult
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.util.coordinates
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DestinationsPresenter @Inject constructor(
        private val mapsApi: GoogleMapsAPI,
        private val tripMapper: TripResultMapper
) {

    fun fetchTrip(
            destination: Destination,
            location: Location
    ): Maybe<TripResult> {
        return mapsApi
                .fetchTrip(location.coordinates, destination.address)
                .subscribeOn(Schedulers.io())
                .map(tripMapper)
                .doOnError { Timber.e(it) }
                .onErrorReturnItem(TripResult.None)
                .observeOn(AndroidSchedulers.mainThread())
    }

}
