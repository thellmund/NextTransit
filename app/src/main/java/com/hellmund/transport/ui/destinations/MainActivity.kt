package com.hellmund.transport.ui.destinations

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hellmund.library.MaterialBottomDialog
import com.hellmund.library.actions.Action
import com.hellmund.library.actions.EnabledAction
import com.hellmund.transport.R
import com.hellmund.transport.data.model.CalendarEvent
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.di.injector
import com.hellmund.transport.di.lazyViewModel
import com.hellmund.transport.ui.destinations.di.DestinationsModule
import com.hellmund.transport.ui.shared.Navigator
import com.hellmund.transport.util.*
import com.hellmund.transport.widget.MaterialBanner
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_placeholder.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(),
        DestinationsAdapter.DragStartListener, DestinationsAdapter.InteractionListener {

    private val compositeDisposables = CompositeDisposable()

    @Inject
    lateinit var viewModelProvider: Provider<MainViewModel>

    @Inject
    lateinit var destinationsPresenter: DestinationsPresenter

    @Inject
    lateinit var navigator: Navigator

    private val destinationsAdapter: DestinationsAdapter by lazy {
        DestinationsAdapter(destinationsPresenter, this)
    }

    private val itemTouchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(DestinationItemTouchHelper(destinationsAdapter))
    }

    private val viewModel: MainViewModel by lazyViewModel { viewModelProvider }

    private val eventBanner: MaterialBanner by lazy {
        MaterialBanner.make(contentContainer)
    }

    private val calendarPromo: MaterialBanner by lazy {
        MaterialBanner.make(contentContainer)
                .setIcon(R.drawable.ic_event_black_24dp)
                .setMessage(R.string.calendar_promo_text)
                .setNegativeButton(R.string.dismiss) { viewModel.denyCalendarAccess() }
                .setPositiveButton(R.string.enable) { showCalendarPermissionDialog() }
    }

    private val notifyWhenToLeaveSnackbar: Snackbar by lazy {
        Snackbar.make(contentContainer, R.string.notification_scheduled, Snackbar.LENGTH_LONG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        injector.destinationsComponent()
                .destinationsModule(DestinationsModule())
                .build()
                .inject(this)

        setupToolbar()
        setupRecyclerView()
        setupButtons()

        if (requiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            navigator.openPermissionDialog(this)
            return
        }

        viewModel.location.observe(this, this::onLocationChange)
        viewModel.viewState.observe(this, this::render)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        recyclerView.onScrolled {
            val isAtTop = it.canScrollVertically(Constants.SCROLL_DIRECTION_DOWN)
            toolbar.isSelected = isAtTop
        }
    }

    private fun setupButtons() {
        placeholderButton.setOnClickListener { navigator.openAdd(this) }
        fab.setOnClickListener { navigator.openAdd(this) }
    }

    private fun render(viewState: MainViewState) {
        swipeRefreshLayout.isEnabled = viewState.destinations.isNotEmpty()
        contentContainer.isVisible = viewState.destinations.isNotEmpty()
        placeholderLayout.isVisible = viewState.destinations.isEmpty()
        destinationsAdapter.updateItems(viewState.destinations)
        fab.isVisible = viewState.destinations.isNotEmpty()

        if (viewState.showNotifySnackbar) {
            notifyWhenToLeaveSnackbar.show()
        } else {
            notifyWhenToLeaveSnackbar.dismiss()
        }

        if (viewState.showCalendarPromo) {
            calendarPromo.show()
        } else {
            calendarPromo.dismiss()
        }

        viewState.nextEvent?.let { (event, trip) ->
            showCalendarBanner(event, trip)
        } ?: eventBanner.dismiss()
    }

    private fun showCalendarBanner(event: CalendarEvent, trip: Trip) {
        eventBanner
                .setIcon(R.drawable.ic_event_black_24dp)
                .setTitle(event.bannerTitle)
                .setMessage(trip.calendarBannerText)
                .setNegativeButton(R.string.dismiss) { viewModel.dismissCalendarBanner() }
                .setPositiveButton(R.string.open_route) { navigator.openRoute(this, event.location, trip) }
                .show()
    }

    private fun onLocationChange(location: Location?) {
        location?.let {
            destinationsAdapter.updateLocation(it)
        } ?: onLocationError()
    }

    private fun onLocationError() {
        TransitionManager.beginDelayedTransition(contentContainer)
        errorLayout.isVisible = true
    }

    private fun showCalendarPermissionDialog() {
        compositeDisposables += RxPermissions(this)
                .request(Manifest.permission.READ_CALENDAR)
                .filter { it }
                .subscribe { viewModel.allowCalendarAccess() }
    }

    private fun setupRecyclerView() {
        swipeRefreshLayout.apply {
            isEnabled = false
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener(this@MainActivity::onRefresh)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = destinationsAdapter
        }

        val spacing = Math.round(resources.getDimension(R.dimen.material_small_padding))
        recyclerView.addItemDecoration(EqualSpacingItemDecoration(spacing))

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun onRefresh() {
        destinationsAdapter.refresh()

        // Add an extra delay, so that the refresh indicator does not immediately
        // disappear again
        Timer().schedule(400) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onClick(destination: Destination) {
        val trip = destination.trip ?: return
        if (trip.hasRoute) {
            navigator.openRoute(this, destination)
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

    private fun openOptionsBottomDialog(destination: Destination) {
        MaterialBottomDialog(this)
                .with(destination)
                .setTitle(destination.title)
                .onSelected { index ->
                    when (index) {
                        0 -> navigator.openEdit(this, destination)
                        1 -> handleNotifyWhenToLeave(destination)
                    }
                }
                .show()
    }

    private fun openDeleteBottomDialog(destination: Destination, position: Int) {
        val title = getString(R.string.remove_destination, destination.title)
        val actions = listOf<Action>(EnabledAction(R.string.remove))

        MaterialBottomDialog(this)
                .with(actions)
                .setTitle(title)
                .onSelected { viewModel.removeDestination(destination) }
                .onDismiss { destinationsAdapter.notifyItemChanged(position) }
                .show()
    }

    private fun handleNotifyWhenToLeave(destination: Destination) {
        viewModel.notifyWhenToLeave(destination)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> navigator.openAbout(this)
        }

        return super.onOptionsItemSelected(item)
    }

}
