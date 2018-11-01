package com.hellmund.transport.ui.route

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hellmund.transport.R
import com.hellmund.transport.data.model.StepItem
import com.hellmund.transport.data.model.TransitStepItem
import com.hellmund.transport.data.model.WalkingStepItem

class StepsAdapter(private val items: List<StepItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.list_item_route_transit_step -> TransitStepItem.ViewHolder(view)
            else -> WalkingStepItem.ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].bindTo(holder)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is TransitStepItem) {
            R.layout.list_item_route_transit_step
        } else {
            R.layout.list_item_route_walking_step
        }
    }

}
