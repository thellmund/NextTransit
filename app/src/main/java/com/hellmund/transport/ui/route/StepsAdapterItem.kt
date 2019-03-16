package com.hellmund.transport.ui.route

import android.graphics.drawable.GradientDrawable
import com.hellmund.transport.R
import com.hellmund.transport.data.model.Step
import kotlinx.android.synthetic.main.list_item_route_transit_step.view.*
import kotlinx.android.synthetic.main.list_item_route_walking_step.view.*

abstract class StepsAdapterItem {
    abstract val layoutId: Int
    abstract fun bind(holder: StepsAdapter.ViewHolder)
}

class TransitStepItem(
        private val step: Step
) : StepsAdapterItem() {

    override val layoutId: Int
        get() = R.layout.list_item_route_transit_step

    override fun bind(holder: StepsAdapter.ViewHolder) = with(holder.itemView) {
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

class WalkingStepItem(
        private val step: Step
) : StepsAdapterItem() {

    override val layoutId: Int
        get() = R.layout.list_item_route_walking_step

    override fun bind(holder: StepsAdapter.ViewHolder) = with(holder.itemView) {
        travelModeIconImageView.setImageResource(step.routeIcon)
        instructionsTextView.text = step.instructions
        durationTextView.text = step.duration.text
    }

}
