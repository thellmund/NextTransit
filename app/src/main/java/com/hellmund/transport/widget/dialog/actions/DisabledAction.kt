package com.hellmund.transport.widget.dialog.actions

import android.view.View

class DisabledAction(labelResId: Int, iconResId: Int) : Action(labelResId, iconResId) {

    override fun onListItemInflated(itemView: View) {
        with(itemView) {
            alpha = 0.5f
        }
    }

}