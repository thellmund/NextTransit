package com.hellmund.transport

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import com.hellmund.transport.di.AppComponent
import com.hellmund.transport.di.AppModule
import com.hellmund.transport.di.DaggerAppComponent
import com.hellmund.transport.util.notifications.RealNotificationScheduler

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        buildAppComponent()
        setupNotificationChannel()
    }

    private fun buildAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    private fun setupNotificationChannel() {
        if (SDK_INT >= O) {
            RealNotificationScheduler.setupNotificationChannel(this)
        }
    }

}
