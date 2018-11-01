package com.hellmund.transport.data.model

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_route_walking_step.view.*

class WalkingStepItem(
        private val step: Step
) : StepItem(step) {

    override fun bindTo(holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as? ViewHolder ?: return
        viewHolder.bind(step)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(step: Step) = with(itemView) {
            travelModeIconImageView.setImageResource(step.routeIcon)
            instructionsTextView.text = step.instructions
            durationTextView.text = step.duration.text
        }

    }

}