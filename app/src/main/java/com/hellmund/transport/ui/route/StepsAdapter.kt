package com.hellmund.transport.ui.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StepsAdapter(
        private val items: List<StepsAdapterItem>
) : RecyclerView.Adapter<StepsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].bind(holder)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].layoutId

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
