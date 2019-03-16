package com.hellmund.transport.util.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hellmund.transport.R
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.ui.route.RouteActivity
import com.hellmund.transport.util.Constants
import com.hellmund.transport.util.bitmap
import javax.inject.Inject

interface NotificationBuilder {
    fun build(destination: Destination, actionIntent: Intent): Notification
}

class RealNotificationBuilder @Inject constructor(
        private val context: Context
): NotificationBuilder {

    override fun build(destination: Destination, actionIntent: Intent): Notification {
        val title = destination.getNotificationTitle(context)
        val text = destination.getNotificationText()

        val trip = destination.trip ?: throw IllegalStateException()

        val launchIntent = Intent(context, RouteActivity::class.java).apply {
            putExtra(Constants.INTENT_TITLE, title)
            putExtra(Constants.INTENT_TRIP, trip)
        }
        val launchPendingIntent = PendingIntent.getActivity(
                context, 1, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        val largeIcon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)

        val action = buildNotificationAction(actionIntent)
        return NotificationCompat.Builder(context, Constants.REMINDERS_CHANNEL)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_directions_transit_white_48dp)
                .setLargeIcon(largeIcon?.bitmap)
                .setContentIntent(launchPendingIntent)
                .addAction(action)
                .setColor(color)
                .setAutoCancel(true)
                .build()
    }

    private fun buildNotificationAction(intent: Intent): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getActivity(
                context, ACTION_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val icon = R.drawable.ic_directions_white_48dp
        val text = context.getString(R.string.show_route)

        return NotificationCompat.Action
                .Builder(icon, text, pendingIntent)
                .build()
    }

    companion object {
        private const val ACTION_INTENT_REQUEST_CODE = 1234
    }

}
