package com.hellmund.transport.ui.destinations

import android.support.v7.util.DiffUtil
import com.hellmund.transport.data.persistence.Destination

class DestinationsDiffUtil(
        private val oldList: List<Destination>,
        private val newList: List<Destination>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}