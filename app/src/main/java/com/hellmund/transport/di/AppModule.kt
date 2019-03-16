package com.hellmund.transport.di

import android.content.Context
import androidx.room.Room
import com.hellmund.transport.data.api.GoogleMapsAPI
import com.hellmund.transport.data.api.GoogleMapsInterceptor
import com.hellmund.transport.data.persistence.AppDatabase
import com.hellmund.transport.data.repos.DestinationsRepository
import com.hellmund.transport.data.repos.TripsRepository
import com.hellmund.transport.ui.destinations.TripMapper
import com.hellmund.transport.ui.destinations.TripResultMapper
import com.hellmund.transport.ui.shared.LocationProvider
import com.hellmund.transport.ui.shared.Navigator
import com.hellmund.transport.ui.shared.RealLocationProvider
import com.hellmund.transport.ui.shared.RealNavigator
import com.hellmund.transport.util.notifications.NotificationBuilder
import com.hellmund.transport.util.notifications.NotificationScheduler
import com.hellmund.transport.util.notifications.RealNotificationBuilder
import com.hellmund.transport.util.notifications.RealNotificationScheduler
import com.patloew.rxlocation.RxLocation
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext(): Context = context

    @Singleton
    @Provides
    fun provideDatabase(): AppDatabase {
        return Room
                .databaseBuilder(context, AppDatabase::class.java, "db")
                .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(GoogleMapsInterceptor())
                .build()
    }

    @Singleton
    @Provides
    fun provideGoogleMapsAPI(
            okHttpClient: OkHttpClient
    ): GoogleMapsAPI {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleMapsAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideTripMapper(
            context: Context
    ) = TripMapper(context)

    @Singleton
    @Provides
    fun provideTripResultMapper(
            context: Context
    ) = TripResultMapper(context)

    @Singleton
    @Provides
    fun provideDestinationsRepository(
            database: AppDatabase
    ) = DestinationsRepository(database)

    @Singleton
    @Provides
    fun provideTripsRepository(
            mapsApi: GoogleMapsAPI,
            tripMapper: TripMapper
    ) = TripsRepository(mapsApi, tripMapper)

    @Singleton
    @Provides
    fun provideNotificationBuilder(
            context: Context
    ): NotificationBuilder = RealNotificationBuilder(context)

    @Singleton
    @Provides
    fun provideNotificationScheduler(
            context: Context
    ): NotificationScheduler = RealNotificationScheduler(context)

    @Singleton
    @Provides
    fun provideRxLocation(
            context: Context
    ) = RxLocation(context)

    @Singleton
    @Provides
    fun provideLocationProvider(
            rxLocation: RxLocation
    ): LocationProvider = RealLocationProvider(rxLocation)

    @Singleton
    @Provides
    fun provideNavigator(
            context: Context
    ): Navigator = RealNavigator(context)

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/maps/"
    }

}
