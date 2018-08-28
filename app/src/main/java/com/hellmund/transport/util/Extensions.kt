package com.hellmund.transport.util

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import com.hellmund.transport.data.model.Coordinates
import java.util.*



// AppCompatActivity

fun AppCompatActivity.requiresPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
}

// RecyclerView.Adapter

val <T : RecyclerView.ViewHolder?> RecyclerView.Adapter<T>.isEmpty: Boolean
    get() = itemCount == 0

val <T : RecyclerView.ViewHolder?> RecyclerView.Adapter<T>.isNotEmpty: Boolean
    get() = itemCount > 0

// Date

val Date.withinNextHour: Boolean
    get () {
        val hourMillis = 60 * 60 * 1000
        val timestamp = time * 1000 // time is in seconds, transform it to milliseconds
        val now = System.currentTimeMillis()
        return timestamp - now <= hourMillis
    }

// Collections

fun <T> MutableList<T>.swapItems(fromPosition: Int, toPosition: Int) {
    if (fromPosition < toPosition) {
        for (i in fromPosition until toPosition) {
            Collections.swap(this, i, i + 1)
        }
    } else {
        for (i in fromPosition downTo toPosition + 1) {
            Collections.swap(this, i, i - 1)
        }
    }
}

// Location

fun Location.toCoordinates() = Coordinates(latitude, longitude)

// Views

fun FloatingActionButton.show(show: Boolean) {
    if (show) {
        show()
    } else {
        hide()
    }
}

fun AppCompatTextView.addCompoundDrawables(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
}

fun AutoCompleteTextView.onChange(callback: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            callback(s?.toString().orEmpty())
        }
    })
}

fun AutoCompleteTextView.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_DONE -> {
                callback()
                true
            }
            else -> false
        }
    }
}

val Drawable.bitmap: Bitmap
    get() {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}
