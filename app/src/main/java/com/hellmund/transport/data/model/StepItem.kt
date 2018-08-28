package com.hellmund.transport.data.model

import android.support.v7.widget.RecyclerView

abstract class StepItem(private val step: Step) {

    abstract fun bindTo(holder: RecyclerView.ViewHolder)

}