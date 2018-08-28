package com.hellmund.transport.widget.dialog.actions

import android.content.Context
import android.view.View
import com.hellmund.transport.R
import com.hellmund.transport.util.addCompoundDrawables
import kotlinx.android.synthetic.main.list_item_bottom_dialog.view.*

abstract class Action(private val labelResId: Int, private val iconResId: Int? = null) {

    fun getListItemView(context: Context): View {
        val itemView = View.inflate(context, R.layout.list_item_bottom_dialog, null).apply {
            textView.text = context.getString(labelResId)

            iconResId?.let {
                textView.addCompoundDrawables(start = it)
            }
        }

        onListItemInflated(itemView)

        return itemView
    }

    open fun onListItemInflated(itemView: View) {
        // Free ad space
    }

}