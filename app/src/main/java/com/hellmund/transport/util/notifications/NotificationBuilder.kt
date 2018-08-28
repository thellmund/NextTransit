package com.hellmund.transport.util.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.hellmund.transport.R
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.ui.route.RouteActivity
import com.hellmund.transport.util.Constants
import com.hellmund.transport.util.bitmap

object NotificationBuilder {

    private const val ACTION_INTENT_REQUEST_CODE = 1234

    fun build(context: Context, destination: Destination, actionIntent: Intent): Notification {
        val title = destination.getNotificationTitle(context)
        val text = destination.getNotificationText(context)

        val launchIntent = Intent(context, RouteActivity::class.java).apply {
            putExtra(Constants.INTENT_TITLE, title)
            putExtra(Constants.INTENT_TRIP, destination.trip)
        }
        val launchPendingIntent = PendingIntent.getActivity(
                context, 1, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        val largeIcon = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round)

        val action = getNotificationAction(context, actionIntent)
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

    private fun getNotificationAction(context: Context,
                                      intent: Intent): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getActivity(
                context, ACTION_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val icon = R.drawable.ic_directions_white_48dp
        val text = context.getString(R.string.show_route)

        return NotificationCompat.Action
                .Builder(icon, text, pendingIntent)
                .build()
    }

}
