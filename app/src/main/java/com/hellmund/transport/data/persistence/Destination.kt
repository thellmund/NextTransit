package com.hellmund.transport.data.persistence

import android.content.Context
import android.os.Parcel
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
import com.hellmund.transport.util.Constants

@Entity(tableName = "destination")
data class Destination(
        @ColumnInfo(name = "position") var position: Int,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "place_id") var placeId: String? = null,
        @Ignore var lastUpdate: Long = 0L,
        @Ignore var trip: Trip? = null
) : Parcelable, Actionable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0

    constructor() : this(0, "", "")

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readParcelable(Trip::class.java.classLoader)) {
        id = parcel.readInt()
    }

    val isRefreshEligible: Boolean
        get() = (System.currentTimeMillis() - lastUpdate) > Constants.ONE_MINUTE

    fun updateWithInput(title: String, address: String) {
        this.title = title
        this.address = address
    }

    fun getNotificationTitle(context: Context): String {
        return String.format(context.getString(R.string.notification_title), title)
    }

    fun getNotificationText(context: Context): String? = trip?.getNotificationText(context)

    override fun getActions(): List<Action> {
        val editAction = EnabledAction(R.string.edit, R.drawable.ic_edit_black_24dp)

        val notifyAction = when (trip) {
            null -> DisabledAction(R.string.notify_when_have_to_leave, R.drawable.ic_notifications_black_24dp)
            else -> EnabledAction(R.string.notify_when_have_to_leave, R.drawable.ic_notifications_black_24dp)
        }

        return arrayListOf(editAction, notifyAction)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val otherDestination = other as Destination
        return title == otherDestination.title
                && address == otherDestination.address
                && trip == otherDestination.trip
    }

    override fun hashCode() = id

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeInt(position)
            writeString(title)
            writeString(address)
            writeString(placeId)
            writeLong(lastUpdate)
            writeParcelable(trip, flags)
            writeInt(id)
        }
    }

    override fun describeContents() = 0

    companion object {

        fun create(position: Int, title: String, address: String): Destination {
            return Destination(position, title, address)
        }

        @JvmField var CREATOR = object : Parcelable.Creator<Destination> {
            override fun createFromParcel(parcel: Parcel) = Destination(parcel)

            override fun newArray(size: Int) = arrayOfNulls<Destination?>(size)
        }

    }

}