package com.hellmund.transport.ui.destinations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.transition.TransitionManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar
import com.hellmund.library.MaterialBottomDialog
import com.hellmund.library.actions.Action
import com.hellmund.library.actions.EnabledAction
import com.hellmund.transport.R
import com.hellmund.transport.data.model.CalendarEvent
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.data.repos.CalendarRepository
import com.hellmund.transport.ui.about.AboutActivity
import com.hellmund.transport.ui.edit.EditDestinationActivity
import com.hellmund.transport.ui.onboarding.IntroductionActivity
import com.hellmund.transport.ui.route.RouteActivity
import com.hellmund.transport.util.*
import com.hellmund.transport.util.notifications.NotificationBuilder
import com.hellmund.transport.util.notifications.NotificationScheduler
import com.hellmund.transport.widget.MaterialBanner
import com.patloew.rxlocation.RxLocation
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_placeholder.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(),
        DestinationsAdapter.DragStartListener, OnDestinationsInteractionListener {

    private var currentBanner: MaterialBanner? = null

    private lateinit var viewModel: MainActivityViewModel
    private var disposables = CompositeDisposable()

    private var adapter = DestinationsAdapter(this)
    private var itemTouchHelper: ItemTouchHelper? = null

    private val calendarManager = CalendarRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setBackgroundDrawable(null)

        initRecyclerView()

        placeholderButton.setOnClickListener { openAddDestination() }
        fab.setOnClickListener { openAddDestination() }

        if (requiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            openIntroductionActivity()
            return
        }

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        viewModel.destinations.observe(this, Observer<List<Destination>> { destinations ->
            destinations?.let { onNewDestinationsAvailable(it) }
        })
    }

    override fun onStart() {
        super.onStart()
        initLocationListener()

        if (!calendarManager.hasDismissedPromo) {
            showCalendarPromo()
        }
    }

    private fun showCalendarPromo() {
        MaterialBanner.make(contentContainer)
                .setIcon(R.drawable.ic_event_black_24dp)
                .setMessage(R.string.calendar_promo_text)
                .setNegativeButton(R.string.dismiss) {
                    currentBanner = null
                }
                .setPositiveButton(R.string.enable) {
                    showCalendarPermissionDialog()
                    currentBanner = null
                }
                .show()
    }

    private fun showCalendarPermissionDialog() {
        val disposable = RxPermissions(this)
                .request(Manifest.permission.READ_CALENDAR)
                .subscribe { granted ->
                    if (granted) {
                        loadNextCalendarEvent()
                    }
                }

        disposables.add(disposable)
    }

    private fun loadNextCalendarEvent() {
        calendarManager.nextEvent.observe(this) { downloadTripForEvent(it) }
    }

    private fun downloadTripForEvent(event: CalendarEvent) {
        viewModel.getTripForEvent(event).observe(this) { trip ->
            if (trip.departureTime.withinNextHour) {
                showNextCalendarEventBanner(event, trip)
            }
        }
    }

    private fun showNextCalendarEventBanner(event: CalendarEvent, trip: Trip) {
        val text = trip.getCalendarBannerText(this)
        MaterialBanner.make(contentContainer)
                .setIcon(R.drawable.ic_event_black_24dp)
                .setTitle(event.getBannerTitle)
                .setMessage(text)
                .setNegativeButton(R.string.dismiss) { banner -> banner.dismiss() }
                .setPositiveButton(R.string.open_route) { openRouteActivity(event.location, trip) }
                .show()
    }

    private fun onNewDestinationsAvailable(results: List<Destination>) {
        swipeRefreshLayout.isEnabled = true
        showDestinationsOrPlaceholder(results)
        invalidateFab()
    }

    private fun showDestinationsOrPlaceholder(destinations: List<Destination>) {
        contentContainer.visibility = if (destinations.isEmpty()) View.GONE else View.VISIBLE
        placeholderLayout.visibility = if (destinations.isEmpty()) View.VISIBLE else View.GONE
        TransitionManager.beginDelayedTransition(contentContainer)
        adapter.updateItems(destinations)
    }

    @SuppressLint("MissingPermission")
    private fun initLocationListener() {
        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(50f)
                .setFastestInterval(Constants.HALF_A_MINUTE)

        val rxLocation = RxLocation(this).apply {
            setDefaultTimeout(15, TimeUnit.SECONDS)
        }

        val disposable = rxLocation
                .location()
                .updates(locationRequest)
                .doOnError { t -> onLocationError(t) }
                .subscribe { location -> onNewLocationAvailable(location) }

        disposables.add(disposable)
    }

    private fun onNewLocationAvailable(newLocation: Location) {
        val isFirstUpdate = viewModel.location == null
        viewModel.location = newLocation
        adapter.updateLocation(newLocation)

        if (isFirstUpdate && calendarManager.hasPermission) {
            loadNextCalendarEvent()
        }
    }

    private fun onLocationError(t: Throwable) {
        Log.d(logTag, "Error in location updates subscription", t)
        TransitionManager.beginDelayedTransition(contentContainer)
        errorLayout.visibility = View.VISIBLE
    }

    private fun openIntroductionActivity() {
        val intent = Intent(this, IntroductionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openRouteActivity(destination: Destination) {
        val trip = destination.trip ?: return
        openRouteActivity(destination.title, trip)
    }

    private fun openRouteActivity(title: String, trip: Trip) {
        val intent = getRouteIntent(title, trip)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        adapter = DestinationsAdapter(this).apply {
            setHasStableIds(true)
        }

        swipeRefreshLayout.apply {
            isEnabled = false
            setColorSchemeResources(R.color.colorAccent)

            setOnRefreshListener {
                adapter.refresh()
                Timer().schedule(400) {
                    isRefreshing = false
                }
            }
        }

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = this@MainActivity.adapter
        }

        val spacing = Math.round(resources.getDimension(R.dimen.material_small_padding))
        recyclerView.addItemDecoration(EqualSpacingItemDecoration(spacing))

        val helper = DestinationItemTouchHelper(adapter)
        itemTouchHelper = ItemTouchHelper(helper).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    override fun onClick(destination: Destination) {
        val trip = destination.trip ?: return
        if (trip.hasRoute) {
            openRouteActivity(destination)
        }
    }

    override fun onOptionsSelected(destination: Destination) {
        openOptionsBottomDialog(destination)
    }

    override fun onDeleteSelected(destination: Destination, position: Int) {
        openDeleteBottomDialog(destination, position)
    }

    override fun onOrderChanged(destinations: List<Destination>) {
        viewModel.updateDestinations(destinations)
    }

    private fun invalidateFab() {
        fab.show(adapter.isNotEmpty)
    }

    private fun openOptionsBottomDialog(destination: Destination) {
        MaterialBottomDialog(this)
                .with(destination)
                .setTitle(destination.title)
                .onSelected { index ->
                    when (index) {
                        0 -> openEditDestinationActivity(destination)
                        1 -> handleNotifyWhenToLeave(destination)
                    }
                }
                .show()
    }

    private fun openEditDestinationActivity(destination: Destination?) {
        val intent = Intent(this, EditDestinationActivity::class.java).apply {
            putExtra(Constants.INTENT_DESTINATION, destination)
        }
        startActivity(intent)
    }

    private fun openDeleteBottomDialog(destination: Destination, position: Int) {
        val actions = listOf<Action>(EnabledAction(R.string.remove, null))

        MaterialBottomDialog(this)
                .with(actions)
                .setTitle(destination.title)
                .onSelected {
                    onConfirmDestinationRemove(destination, position)
                    invalidateFab()
                }
                .onDismiss {
                    adapter.notifyItemChanged(position)
                }
                .show()
    }

    private fun onConfirmDestinationRemove(destination: Destination, position: Int) {
        viewModel.removeDestination(destination)
        adapter.remove(position)

        if (adapter.isEmpty) {
            contentContainer.visibility = View.GONE
            placeholderLayout.visibility = View.VISIBLE
        }

        invalidateFab()
    }

    private fun handleNotifyWhenToLeave(destination: Destination) {
        val trip = destination.trip ?: return
        val minsToDeparture = trip.departureTime.minutesUntil
        val minsToStop = trip.minutesToDepartureStop
        val buffer = (minsToDeparture - minsToStop - 2) * 60 * 1000

        val routeIntent = getRouteIntent(destination) ?: return
        val notificationTime = SystemClock.elapsedRealtime() + buffer
        val notification = NotificationBuilder.build(this, destination, routeIntent)
        NotificationScheduler.schedule(this, destination.id, notification, notificationTime)

        Snackbar.make(contentContainer, getString(R.string.notification_scheduled), Snackbar.LENGTH_LONG)
                .show()
    }

    private fun getRouteIntent(destination: Destination): Intent? {
        val trip = destination.trip ?: return null
        return getRouteIntent(destination.title, trip)
    }

    private fun getRouteIntent(title: String, trip: Trip): Intent {
        return Intent(this, RouteActivity::class.java).apply {
            putExtra(Constants.INTENT_TITLE, title)
            putExtra(Constants.INTENT_TRIP, trip)
            putExtra(Constants.INTENT_LOCATION, viewModel.location)
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }


    private fun openAddDestination() {
        val intent = Intent(this, EditDestinationActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> openAbout()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        disposables.dispose()
    }

    companion object {
        private val logTag = MainActivity::class.java.simpleName
    }

}