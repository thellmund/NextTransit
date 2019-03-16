package com.hellmund.transport.di

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.hellmund.transport.App

val Context.app: App
    get() = applicationContext as App

val AppCompatActivity.injector: AppComponent
    get() = app.appComponent
