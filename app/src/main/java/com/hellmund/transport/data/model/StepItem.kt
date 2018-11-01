package com.hellmund.transport.data.model

import androidx.recyclerview.widget.RecyclerView

abstract class StepItem(private val step: Step) {

    abstract fun bindTo(holder: RecyclerView.ViewHolder)

}