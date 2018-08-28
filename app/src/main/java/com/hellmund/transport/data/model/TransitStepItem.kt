package com.hellmund.transport.data.model

import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_route_transit_step.view.*

class TransitStepItem(private val step: Step) : StepItem(step) {

    override fun bindTo(holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as? ViewHolder ?: return
        viewHolder.bind(step)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(step: Step) = with(itemView) {
            transitModeIconImageView.setImageResource(step.routeIcon)
            departureStopNameTextView.text = step.transitDetails?.departureStop?.name
            signTextView.text = step.transitDetails?.line?.shortNameOrName

            val background = signTextView.background as? GradientDrawable
            val color = step.transitDetails?.line?.hexColor
            color?.let { background?.setColor(it) }

            transitDetailsTextView.text = step.getTransitDetails(context)
            departureTimeTextView.text = step.transitDetails?.departureTime?.text
            transitDurationTextView.text = step.duration.text
        }

    }

}