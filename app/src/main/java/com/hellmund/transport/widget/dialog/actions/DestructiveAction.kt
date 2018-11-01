package com.hellmund.transport.widget.dialog.actions

import androidx.core.content.ContextCompat
import android.view.View
import com.hellmund.transport.R
import kotlinx.android.synthetic.main.list_item_bottom_dialog.view.*
import org.jetbrains.anko.textColor

class DestructiveAction(labelResId: Int, iconResId: Int) : Action(labelResId, iconResId) {

    override fun onListItemInflated(itemView: View) {
        with(itemView) {
            textView.textColor = ContextCompat.getColor(context, R.color.errorText)
        }
    }

}