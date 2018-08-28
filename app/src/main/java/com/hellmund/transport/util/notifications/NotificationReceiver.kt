package com.hellmund.transport.util.notifications

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hellmund.transport.util.Constants
import org.jetbrains.anko.notificationManager

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0)
        val notification = intent.getParcelableExtra<Notification>(Constants.KEY_NOTIFICATION)

        val notificationManager = context.notificationManager
        notificationManager.notify(notificationId, notification)
    }

}
