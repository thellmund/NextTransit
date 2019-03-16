package com.hellmund.transport.ui.edit

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.chip.Chip
import com.hellmund.transport.R
import org.jetbrains.anko.textColor

class NameSuggestionChip @JvmOverloads constructor(
        context: Context,
        name: String = "",
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : Chip(context, attrs, defStyle) {

    init {
        text = name
        isCheckable = true
        isClickable = true
        isCheckedIconVisible = false
        textColor = Color.WHITE
        setChipBackgroundColorResource(R.color.chip_color)
    }

}
