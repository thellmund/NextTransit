package com.hellmund.transport.util.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.hellmund.transport.BuildConfig
import com.hellmund.transport.R
import com.hellmund.transport.util.Constants
import org.jetbrains.anko.alarmManager
import org.jetbrains.anko.notificationManager
import javax.inject.Inject

interface NotificationScheduler {
    fun schedule(notificationId: Int, notification: Notification, timestamp: Long)
}

class RealNotificationScheduler @Inject constructor(
        private val context: Context
) : NotificationScheduler {

    override fun schedule(notificationId: Int, notification: Notification, timestamp: Long) {
        val notificationTime =
                if (BuildConfig.DEBUG) (SystemClock.elapsedRealtime() + 5000)
                else timestamp

        val alarmIntent = getAlarmIntent(notificationId, notification)
        val alarmManager = context.alarmManager
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, notificationTime, alarmIntent)
    }

    private fun getAlarmIntent(notificationId: Int, notification: Notification): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(Constants.KEY_NOTIFICATION_ID, notificationId)
            putExtra(Constants.KEY_NOTIFICATION, notification)
        }
        return PendingIntent.getBroadcast(context,
                INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    companion object {

        private const val INTENT_REQUEST_CODE = 0

        @RequiresApi(api = 26)
        fun setupNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                    Constants.REMINDERS_CHANNEL,
                    context.getString(R.string.reminders_channel_name),
                    NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
            }

            val notificationManager = context.notificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

}