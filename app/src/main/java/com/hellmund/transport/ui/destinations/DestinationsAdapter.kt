package com.hellmund.transport.ui.destinations

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.transport.R
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.util.swap

class DestinationsAdapter(
        private val presenter: DestinationsPresenter,
        private val listener: InteractionListener
) : RecyclerView.Adapter<DestinationViewHolder>(), DestinationItemTouchHelper.Listener {

    private val items = mutableListOf<Destination>()
    private var location: Location? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_destination, parent, false)
        return DestinationViewHolder(view, presenter)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(items[position], location, listener)
    }

    override fun getItemCount() = items.size

    override fun onMove(fromPosition: Int, toPosition: Int) {
        items.swap(fromPosition, toPosition)
        items.mapIndexed { index, item -> item.copy(position = index) }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onMoveFinished() {
        listener.onOrderChanged(items)
    }

    override fun onSwipedLeft(position: Int) {
        listener.onDeleteSelected(items[position], position)
    }

    override fun onSwipedRight(position: Int) {
        listener.onDeleteSelected(items[position], position)
    }

    fun updateItems(newItems: List<Destination>) {
        newItems.forEach { newItem ->
            val oldItem = items.firstOrNull { it.id == newItem.id }
            oldItem?.let {
                newItem.tripResult = it.tripResult
            }
        }

        val diffUtil = DestinationsDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    fun refresh() {
        items.forEach { it.reset() }
        notifyDataSetChanged()
    }

    fun updateLocation(location: Location?) {
        this.location = location
        refresh()
    }

    override fun onViewDetachedFromWindow(holder: DestinationViewHolder) {
        holder.onDetached()
    }

    interface DragStartListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    interface InteractionListener {
        fun onClick(destination: Destination)
        fun onOptionsSelected(destination: Destination)
        fun onDeleteSelected(destination: Destination, position: Int)
        fun onOrderChanged(destinations: List<Destination>)
    }

}
