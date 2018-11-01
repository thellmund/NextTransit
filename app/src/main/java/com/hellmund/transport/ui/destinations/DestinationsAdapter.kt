package com.hellmund.transport.ui.destinations

import android.content.Context
import android.graphics.PorterDuff
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellmund.transport.R
import com.hellmund.transport.data.model.Trip
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.util.swapItems
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_item_destination.view.*
import java.util.concurrent.TimeUnit

class DestinationsAdapter(context: Context) :
        RecyclerView.Adapter<DestinationsAdapter.ViewHolder>(),
        DestinationItemTouchHelper.ItemTouchHelperListener {

    private var listener = context as OnDestinationsInteractionListener

    private val presenter = DestinationsPresenter(context)
    private var items = mutableListOf<Destination>()

    private var location: Location? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_destination, parent, false)
        return ViewHolder(view, presenter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = items[position]
        holder.bind(destination, location, listener)
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].id.toLong()

    override fun onMove(fromPosition: Int, toPosition: Int) {
        items.swapItems(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        items.forEachIndexed { index, item ->
            item.position = index
        }

        listener.onOrderChanged(items)
    }

    override fun onSwipedLeft(position: Int) {
        listener.onDeleteSelected(items[position], position)
    }

    override fun onSwipedRight(position: Int) {
        listener.onDeleteSelected(items[position], position)
    }

    fun remove(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateItems(newDestinations: List<Destination>) {
        val diffUtil = DestinationsDiffUtil(items, newDestinations)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        items.clear()
        items.addAll(newDestinations)

        diffResult.dispatchUpdatesTo(this)
    }

    fun refresh() {
        items.forEach {
            it.trip = null
            it.lastUpdate = 0L
        }
        notifyDataSetChanged()
    }

    fun updateLocation(location: Location?) {
        this.location = location
        notifyDataSetChanged()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.stopAutoRefresh()
    }

    class ViewHolder(
            itemView: View,
            private val presenter: DestinationsPresenter
    ) : RecyclerView.ViewHolder(itemView), DestinationsView {

        private val disposables = CompositeDisposable()

        fun bind(destination: Destination, location: Location?,
                 listener: OnDestinationsInteractionListener) = with(itemView) {
            nameTextView.text = destination.title

            setOnClickListener { listener.onClick(destination) }
            optionsButton.setOnClickListener { listener.onOptionsSelected(destination) }

            val disposable = Observable.interval(60, TimeUnit.SECONDS)
                    .startWith(0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        refreshView(destination, location)
                    }
            disposables.add(disposable)
        }

        private fun refreshView(destination: Destination, location: Location?) {
            val trip = destination.trip
            when {
                destination.isRefreshEligible -> {
                    showLoading()
                    presenter.fetchTrip(this, destination, location)
                }
                trip == null -> showError()
                else -> showResult(trip)
            }
        }

        override fun showLoading() = with(itemView) {
            colorAccentView.visibility = View.INVISIBLE
            loadingProgressBar.visibility = View.VISIBLE
            durationUntilDepartureContainer.visibility = View.INVISIBLE
            transitLineTextView.visibility = View.INVISIBLE
            errorTextView.visibility = View.GONE
        }

        override fun showResult(trip: Trip) = with(itemView) {
            val durationText = trip.getFormattedMinutesUntilDeparture(context)
            val unitText = trip.getUnitsText(context)

            durationUntilDepartureTextView.text = durationText
            unitTextView.text = unitText
            durationUntilDepartureContainer.visibility = View.VISIBLE

            transitLineTextView.visibility = View.VISIBLE
            transitLineTextView.text = trip.getTransitLineInformation(context)

            loadingProgressBar.visibility = View.GONE
            errorTextView.visibility = View.GONE

            colorAccentView.visibility = View.VISIBLE
            colorAccentView.background.setColorFilter(
                    ContextCompat.getColor(context, trip.durationColor),
                    PorterDuff.Mode.SRC_ATOP
            )
        }

        override fun showError() = with(itemView) {
            val transparent = ContextCompat.getColor(context, android.R.color.transparent)
            colorAccentView.setBackgroundColor(transparent)
            colorAccentView.visibility = View.VISIBLE

            durationUntilDepartureContainer.visibility = View.INVISIBLE
            loadingProgressBar.visibility = View.GONE

            transitLineTextView.visibility = View.GONE
            errorTextView.visibility = View.VISIBLE
        }

        fun stopAutoRefresh() {
            disposables.dispose()
        }

    }

    interface DragStartListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

}
