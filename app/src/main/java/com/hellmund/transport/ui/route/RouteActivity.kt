package com.hellmund.transport.ui.route

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hellmund.transport.R
import com.hellmund.transport.data.api.GoogleMapsClient
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.di.injector
import com.hellmund.transport.ui.shared.LocationProvider
import com.hellmund.transport.util.Constants
import kotlinx.android.synthetic.main.activity_route.*
import org.jetbrains.anko.browse
import javax.inject.Inject

class RouteActivity : AppCompatActivity() {

    private val title: String by lazy {
        intent.getStringExtra(Constants.INTENT_TITLE) ?: throw IllegalStateException()
    }

    private val trip: Trip by lazy {
        intent.getParcelableExtra<Trip>(Constants.INTENT_TRIP) ?: throw IllegalStateException()
    }

    @Inject
    lateinit var locationProvider: LocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        injector.inject(this)

        setupToolbar()
        setupRecyclerView()
        setupBottomBar()

        navigationDepartureStopButton.setOnClickListener {
            navigateToDepartureStop(trip)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        val route = trip.route ?: return
        val stepItems = route.legs.first().steps.map { it.toStepItem() }

        routeRecyclerView.apply {
            setHasFixedSize(true)
            adapter = StepsAdapter(stepItems)
            layoutManager = LinearLayoutManager(this@RouteActivity)
            itemAnimator = DefaultItemAnimator()
        }

        val itemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        routeRecyclerView.addItemDecoration(itemDecoration)
    }

    private fun setupBottomBar() {
        val arrivalTime = trip.arrivalTime.text
        arrivalPredictionTextView.text = getString(R.string.arrival_prediction, title, arrivalTime)
    }

    @SuppressLint("MissingPermission")
    private fun navigateToDepartureStop(trip: Trip) {
        val location = locationProvider.lastLocation ?: return
        val url = GoogleMapsClient.routeToDepartureStopUrl(location, trip.departureStop)
        browse(url)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_route, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}