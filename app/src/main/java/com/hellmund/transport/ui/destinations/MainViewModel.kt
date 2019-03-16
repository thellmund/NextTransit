package com.hellmund.transport.ui.destinations

import android.annotation.SuppressLint
import android.location.Location
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hellmund.transport.data.model.CalendarEvent
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.data.repos.CalendarHelper
import com.hellmund.transport.data.repos.DestinationsRepository
import com.hellmund.transport.data.repos.TripsRepository
import com.hellmund.transport.ui.shared.LocationProvider
import com.hellmund.transport.ui.shared.Navigator
import com.hellmund.transport.util.notifications.NotificationBuilder
import com.hellmund.transport.util.notifications.RealNotificationScheduler
import com.hellmund.transport.util.plusAssign
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class Action {
    data class DatabaseChange(val destinations: List<Destination>) : Action()
    data class ReorderDestinations(val destinations: List<Destination>) : Action()
    data class DeleteDestination(val destination: Destination) : Action()
    object Refresh : Action()
    object ShowCalendarPromo : Action()
    object HideCalendarPromo : Action()
    object AllowedCalendarAccess : Action()
    object DeniedCalendarAccess : Action()
    data class UpcomingEventDiscovered(val event: CalendarEvent, val trip: Trip) : Action()
    object HideCalendarBanner : Action()
    data class NotifyWhenToLeave(val destination: Destination) : Action()
}

sealed class Result {
    object None : Result()
    object ShowLoading : Result()
    data class Data(val destinations: List<Destination>) : Result()
    data class UpcomingEvent(val event: CalendarEvent, val trip: Trip) : Result()
    object ShowCalendarPromo : Result()
    object HideCalendarPromo : Result()
    object HideLoading : Result()
    object HideCalendarBanner : Result()
    object ShowNotifySnackbar : Result()
    object HideNotifySnackbar : Result()
}

class MainViewModel @Inject constructor(
        private val destinationsRepo: DestinationsRepository,
        private val tripRepo: TripsRepository,
        private val calendarHelper: CalendarHelper,
        private val locationProvider: LocationProvider,
        private val navigator: Navigator,
        private val notificationBuilder: NotificationBuilder,
        private val notificationScheduler: RealNotificationScheduler
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val eventsRelay = PublishRelay.create<Action>()

    private val _viewState = MutableLiveData<MainViewState>()
    val viewState: LiveData<MainViewState> = _viewState

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    init {
        val initialViewState = MainViewState.initial()
        val databaseChanges = destinationsRepo.destinations.map { Action.DatabaseChange(it) }

        compositeDisposable += setupLocationUpdates()
                .doOnNext { loadNextCalendarEvent(it) }
                .subscribe(_location::postValue)

        val calendarEvents = calendarHelper.events.map {
            when (it) {
                CalendarHelper.Event.HAS_NOT_DISMISSED -> Action.ShowCalendarPromo
                CalendarHelper.Event.HAS_DISMISSED -> Action.HideCalendarPromo
            }
        }

        compositeDisposable += Observable.merge(databaseChanges, eventsRelay, calendarEvents)
                .switchMap(this::processAction)
                .scan(initialViewState, this::reduceState)
                .subscribe(this::render)
    }

    private fun loadNextCalendarEvent(location: Location) {
        if (calendarHelper.hasPermission.not()) {
            return
        }

        compositeDisposable += calendarHelper.loadNextEvent()
                .flatMap { fetchTripForEvent(location, it) }
                .filter { (_, trip) -> trip.departureTime.withinNextHour }
                .map { (event, trip) -> Action.UpcomingEventDiscovered(event, trip) }
                .subscribe(eventsRelay::accept, Timber::e)
    }

    private fun fetchTripForEvent(
            location: Location,
            calendarEvent: CalendarEvent
    ): Maybe<Pair<CalendarEvent, Trip>> {
        return tripRepo
                .fetchTrip(location, calendarEvent.location, calendarEvent.timestamp)
                .map { calendarEvent to it }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationUpdates(): Observable<Location> {
        return locationProvider.lastLocationUpdates
    }

    private fun processAction(action: Action): Observable<Result> {
        return when (action) {
            is Action.DatabaseChange -> Observable.just(Result.Data(action.destinations))
            is Action.DeleteDestination -> deleteDestination(action.destination)
            is Action.ReorderDestinations -> reorderDestinations(action.destinations)
            Action.Refresh -> Observable.just(Result.ShowLoading)
            Action.ShowCalendarPromo -> Observable.just(Result.ShowCalendarPromo)
            Action.HideCalendarPromo -> Observable.just(Result.HideCalendarPromo)
            Action.AllowedCalendarAccess -> Observable.just(Result.HideCalendarPromo)
            Action.DeniedCalendarAccess -> Observable.just(Result.HideCalendarPromo)
            is Action.UpcomingEventDiscovered -> Observable.just(Result.UpcomingEvent(action.event, action.trip))
            Action.HideCalendarBanner -> Observable.just(Result.HideCalendarBanner)
            is Action.NotifyWhenToLeave -> handleNotifyWhenToLeave(action.destination)
        }
    }

    private fun deleteDestination(destination: Destination): Observable<Result> {
        return Completable
                .fromAction { destinationsRepo.removeDestination(destination) }
                .andThen(Observable.just(Result.None as Result))
    }

    private fun reorderDestinations(destinations: List<Destination>): Observable<Result> {
        return Completable
                .fromAction { destinationsRepo.updateDestinations(destinations) }
                .andThen(Observable.just(Result.None as Result))
    }

    private fun handleNotifyWhenToLeave(destination: Destination): Observable<Result> {
        val trip = destination.trip ?: throw IllegalStateException()
        val minsToDeparture = trip.departureTime.minutesUntil
        val minsToStop = trip.minutesToDepartureStop
        val buffer = (minsToDeparture - minsToStop - 2) * 60 * 1000

        val routeIntent = navigator.getRouteIntent(destination) ?: throw IllegalStateException()
        val notificationTime = SystemClock.elapsedRealtime() + buffer
        val notification = notificationBuilder.build(destination, routeIntent)
        notificationScheduler.schedule(destination.id, notification, notificationTime)

        return Observable.timer(4, TimeUnit.SECONDS)
                .map { Result.HideNotifySnackbar as Result }
                .startWith(Result.ShowNotifySnackbar)
    }

    private fun reduceState(
            viewState: MainViewState,
            result: Result
    ): MainViewState {
        return when (result) {
            Result.ShowLoading -> viewState.copy(isLoading = true)
            is Result.Data -> viewState.copy(destinations = result.destinations)
            Result.HideLoading -> viewState.copy(isLoading = false)
            Result.ShowCalendarPromo -> viewState.copy(showCalendarPromo = true)
            Result.HideCalendarPromo -> viewState.copy(showCalendarPromo = false)
            is Result.UpcomingEvent -> viewState.copy(nextEvent = result.event to result.trip)
            Result.HideCalendarBanner -> viewState.copy(nextEvent = null)
            Result.ShowNotifySnackbar -> viewState.copy(showNotifySnackbar = true)
            Result.HideNotifySnackbar -> viewState.copy(showNotifySnackbar = false)
            Result.None -> viewState
        }
    }

    fun allowCalendarAccess() {
        eventsRelay.accept(Action.AllowedCalendarAccess)
    }

    fun denyCalendarAccess() {
        eventsRelay.accept(Action.DeniedCalendarAccess)
    }

    fun dismissCalendarBanner() {
        eventsRelay.accept(Action.HideCalendarBanner)
    }

    fun updateDestinations(destinations: List<Destination>) {
        eventsRelay.accept(Action.ReorderDestinations(destinations))
    }

    fun removeDestination(destination: Destination) {
        eventsRelay.accept(Action.DeleteDestination(destination))
    }

    fun notifyWhenToLeave(destination: Destination) {
        eventsRelay.accept(Action.NotifyWhenToLeave(destination))
    }

    private fun render(viewState: MainViewState) {
        _viewState.postValue(viewState)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
