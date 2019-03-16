package com.hellmund.transport.ui.destinations.di

import android.content.Context
import com.hellmund.transport.data.api.GoogleMapsAPI
import com.hellmund.transport.data.repos.CalendarHelper
import com.hellmund.transport.ui.destinations.DestinationsPresenter
import com.hellmund.transport.ui.destinations.TripResultMapper
import dagger.Module
import dagger.Provides

@Module
class DestinationsModule {

    @Provides
    fun provideCalendarHelper(
            context: Context
    ): CalendarHelper {
        return CalendarHelper(context)
    }

    @Provides
    fun provideDestinationsPresenter(
            mapsApi: GoogleMapsAPI,
            tripMapper: TripResultMapper
    ): DestinationsPresenter {
        return DestinationsPresenter(mapsApi, tripMapper)
    }

}
