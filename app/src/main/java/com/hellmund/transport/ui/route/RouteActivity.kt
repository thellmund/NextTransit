package com.hellmund.transport.ui.route

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.hellmund.transport.R
import com.hellmund.transport.data.api.GoogleMapsClient
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.util.Constants
import kotlinx.android.synthetic.main.activity_route.*
import org.jetbrains.anko.browse

class RouteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)
        window.setBackgroundDrawable(null)

        val title = intent.getStringExtra(Constants.INTENT_TITLE)
        val trip = intent.getParcelableExtra<Trip>(Constants.INTENT_TRIP)

        setupToolbar(title)
        setupRecyclerView(trip)
        setupBottomBar(title, trip)

        navigationDepartureStopButton.setOnClickListener {
            navigateToDepartureStop(trip)
        }
    }

    private fun setupToolbar(title: String) {
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView(trip: Trip) {
        val route = trip.route ?: return
        val steps = route.legs.first().steps.map { it.stepItem }

        routeRecyclerView.apply {
            setHasFixedSize(true)
            adapter = StepsAdapter(steps)
            layoutManager = LinearLayoutManager(this@RouteActivity)
            itemAnimator = DefaultItemAnimator()
        }

        val itemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        routeRecyclerView.addItemDecoration(itemDecoration)
    }

    private fun setupBottomBar(title: String, trip: Trip) {
        val arrivalTime = trip.arrivalTime.text
        arrivalPredictionTextView.text =
                String.format(getString(R.string.arrival_prediction), title, arrivalTime)
    }

    private fun navigateToDepartureStop(trip: Trip) {
        val location = intent.getParcelableExtra<Location>(Constants.INTENT_LOCATION) ?: return
        val url = GoogleMapsClient.routeToDepartureStopURL(location, trip.departureStop)
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