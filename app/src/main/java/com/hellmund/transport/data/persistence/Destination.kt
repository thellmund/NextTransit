package com.hellmund.transport.data.persistence

import android.content.Context
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.hellmund.library.actions.Action
import com.hellmund.library.actions.Actionable
import com.hellmund.library.actions.DisabledAction
import com.hellmund.library.actions.EnabledAction
import com.hellmund.transport.R
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.model.TripResult
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "destination")
data class Destination(
        @ColumnInfo(name = "position") val position: Int = 0,
        @ColumnInfo(name = "title") val title: String = "",
        @ColumnInfo(name = "address") val address: String = "",
        @ColumnInfo(name = "place_id") val placeId: String? = null
) : Parcelable, Actionable {

    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    @Ignore
    @IgnoredOnParcel
    var tripResult: TripResult = TripResult.Loading

    val trip: Trip?
        get() = (tripResult as? TripResult.Success)?.trip

    fun reset() {
        tripResult = TripResult.Loading
    }

    fun updateWithInput(title: String, address: String): Destination {
        return copy(title = title, address = address)
    }

    fun getNotificationTitle(context: Context): String {
        return context.getString(R.string.notification_title, title)
    }

    fun getNotificationText(): String? {
        val result = tripResult
        return when (result) {
            is TripResult.Success -> result.trip.notificationText
            else -> null
        }
    }

    override fun getActions(): List<Action> {
        val editAction = EnabledAction(R.string.edit, R.drawable.ic_outline_create_24px)

        val notifyAction = when (tripResult) {
            is TripResult.Success -> EnabledAction(R.string.notify_when_have_to_leave, R.drawable.ic_outline_notifications_none_24px)
            else -> DisabledAction(R.string.notify_when_have_to_leave, R.drawable.ic_outline_notifications_none_24px)
        }

        return arrayListOf(editAction, notifyAction)
    }

    companion object {

        fun create(position: Int, title: String, address: String): Destination {
            return Destination(position, title, address)
        }

    }

}