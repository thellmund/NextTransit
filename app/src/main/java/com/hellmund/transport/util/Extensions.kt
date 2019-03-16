package com.hellmund.transport.util

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.hellmund.transport.data.model.Coordinates
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

// AppCompatActivity

fun AppCompatActivity.requiresPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
}

// Date

val Date.isWithinNextHour: Boolean
    get () {
        val hourMillis = 60 * 60 * 1000
        val timestamp = time * 1000 // time is in seconds, transform it to milliseconds
        val now = System.currentTimeMillis()
        return timestamp - now <= hourMillis
    }

// Collections

fun <T> MutableList<T>.swap(fromPosition: Int, toPosition: Int) {
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

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, androidx.lifecycle.Observer { observer(it) })
}

val Location.coordinates: String
    get() = this.toCoordinates().toString()

fun ChipGroup.onCheckedChanged(block: (String) -> Unit) {
    setOnCheckedChangeListener { chipGroup, checkedId ->
        val chip = chipGroup.findViewById<Chip>(checkedId)
        block(chip.text.toString())
    }
}

fun RecyclerView.onScrolled(block: (RecyclerView) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            block(recyclerView)
        }
    })
}
