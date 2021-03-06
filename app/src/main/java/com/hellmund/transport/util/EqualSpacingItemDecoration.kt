package com.hellmund.transport.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EqualSpacingItemDecoration(
        private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val lastPosition = state.itemCount - 1

        outRect.apply {
            left = spacing
            right = spacing
            top = spacing
            bottom = if (position == lastPosition) spacing else 0
        }
    }

}
