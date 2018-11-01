package com.hellmund.transport.widget.dialog

import androidx.appcompat.app.AppCompatActivity
import com.hellmund.transport.ui.destinations.MaterialBottomDialogFragment
import com.hellmund.transport.ui.destinations.MaterialBottomDialogListener
import com.hellmund.transport.widget.dialog.actions.Action
import com.hellmund.transport.widget.dialog.actions.Actionable

class MaterialBottomDialog(private val activity: AppCompatActivity) {

    private var onClickListener: ((Int) -> Unit)? = null
    private var onCancelListener: (() -> Unit)? = null

    private var actions = listOf<Action>()

    fun with(actions: List<Action>): MaterialBottomDialog {
        this.actions = actions
        return this
    }

    fun with(actionable: Actionable): MaterialBottomDialog {
        return with(actionable.getOptionsActions())
    }

    fun onSelected(callback: (Int) -> Unit): MaterialBottomDialog {
        onClickListener = callback
        return this
    }

    fun onDismiss(callback: () -> Unit): MaterialBottomDialog {
        onCancelListener = callback
        return this
    }

    fun show() {
        val listener = object : MaterialBottomDialogListener {
            override fun onOptionSelected(index: Int) {
                onClickListener?.invoke(index)
            }

            override fun onDismissed() {
                onCancelListener?.invoke()
            }
        }

        val fragment = MaterialBottomDialogFragment.newInstance(actions, listener)
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

}