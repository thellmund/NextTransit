package com.hellmund.transport.ui.shared

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.ui.about.AboutActivity
import com.hellmund.transport.ui.destinations.MainActivity
import com.hellmund.transport.ui.edit.EditDestinationActivity
import com.hellmund.transport.ui.onboarding.IntroductionActivity
import com.hellmund.transport.ui.route.RouteActivity
import com.hellmund.transport.util.Constants

interface Navigator {
    fun openPermissionDialog(activity: Activity)
    fun openAdd(activity: Activity)
    fun openEdit(activity: Activity, destination: Destination)
    fun openRoute(activity: Activity, destination: Destination)
    fun openRoute(activity: Activity, title: String, trip: Trip)
    fun getRouteIntent(destination: Destination): Intent?
    fun finishOnboarding(activity: Activity)
    fun openAbout(activity: Activity)
}

class RealNavigator(
        private val context: Context
) : Navigator {

    override fun openPermissionDialog(activity: Activity) {
        val intent = Intent(activity, IntroductionActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    override fun openAdd(activity: Activity) {
        val intent = Intent(activity, EditDestinationActivity::class.java)
        activity.startActivity(intent)
    }

    override fun openEdit(activity: Activity, destination: Destination) {
        val intent = Intent(activity, EditDestinationActivity::class.java).apply {
            putExtra(Constants.INTENT_DESTINATION, destination)
        }
        activity.startActivity(intent)
    }

    override fun openRoute(activity: Activity, destination: Destination) {
        val trip = destination.trip ?: throw IllegalStateException()
        openRoute(activity, destination.title, trip)
    }

    override fun openRoute(activity: Activity, title: String, trip: Trip) {
        val intent = buildRouteIntent(title, trip)
        activity.startActivity(intent)
    }

    override fun getRouteIntent(destination: Destination): Intent? {
        val trip = destination.trip ?: return null
        return buildRouteIntent(destination.title, trip)
    }

    private fun buildRouteIntent(title: String, trip: Trip): Intent {
        return Intent(context, RouteActivity::class.java).apply {
            putExtra(Constants.INTENT_TITLE, title)
            putExtra(Constants.INTENT_TRIP, trip)
        }
    }

    override fun finishOnboarding(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    override fun openAbout(activity: Activity) {
        val intent = Intent(activity, AboutActivity::class.java)
        activity.startActivity(intent)
    }

}
