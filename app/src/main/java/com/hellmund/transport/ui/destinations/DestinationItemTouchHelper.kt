package com.hellmund.transport.ui.destinations

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

class DestinationItemTouchHelper(
        private val listener: ItemTouchHelperListener
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(view: RecyclerView, holder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = true

    override fun onMove(view: RecyclerView,
                        holder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = holder.adapterPosition
        val toPosition = target.adapterPosition
        listener.onMove(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
        val position = holder.adapterPosition
        when (direction) {
            ItemTouchHelper.START -> listener.onSwipedLeft(position)
            ItemTouchHelper.END -> listener.onSwipedRight(position)
        }
    }

    override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView,
                             holder: RecyclerView.ViewHolder, dX: Float,
                             dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (holder.adapterPosition == RecyclerView.NO_POSITION) {
            return
        }

        val itemWidth = holder.itemView.width
        val swipedWidth = Math.abs(dX)
        val remainingWidthPercentage = 1f - swipedWidth / itemWidth.toFloat()
        holder.itemView.alpha = remainingWidthPercentage

        super.onChildDraw(canvas, recyclerView, holder, dX, dY, actionState, isCurrentlyActive)
    }

    interface ItemTouchHelperListener {
        fun onMove(fromPosition: Int, toPosition: Int)
        fun onSwipedLeft(position: Int)
        fun onSwipedRight(position: Int)
    }

}
