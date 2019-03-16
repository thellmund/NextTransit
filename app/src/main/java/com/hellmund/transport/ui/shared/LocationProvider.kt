package com.hellmund.transport.ui.shared

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.hellmund.transport.util.Constants
import com.patloew.rxlocation.RxLocation
import io.reactivex.Observable
import timber.log.Timber

interface LocationProvider {
    val lastLocation: Location?
    val lastLocationUpdates: Observable<Location>
}

@SuppressLint("MissingPermission")
class RealLocationProvider(
        private val rxLocation: RxLocation
) : LocationProvider {

    override val lastLocation: Location?
        get() {
            return try {
                rxLocation.location().lastLocation().blockingGet()
            } catch (t: Throwable) {
                Timber.w(t)
                null
            }
        }

    override val lastLocationUpdates: Observable<Location>
        get() {
            val locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setSmallestDisplacement(50f)
                    .setFastestInterval(Constants.HALF_A_MINUTE)

            return rxLocation.location().updates(locationRequest)
        }

}
