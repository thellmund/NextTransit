package com.hellmund.transport.ui.destinations

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hellmund.transport.widget.dialog.actions.Action
import com.hellmund.transport.widget.dialog.actions.CancelAction

class MaterialBottomDialogFragment : RoundedBottomSheetDialogFragment() {

    lateinit var actions: List<Action>
    lateinit var listener: MaterialBottomDialogListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }

        actions.forEach { action ->
            val index = contentView.childCount
            val view = action.getListItemView(requireContext()).apply {
                setOnClickListener {
                    when (action) {
                        is CancelAction -> dismiss()
                        else -> handleDialogItemClick(index)
                    }
                }
            }
            contentView.addView(view)
        }

        return contentView
    }

    private fun handleDialogItemClick(position: Int) {
        listener.onOptionSelected(position)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        listener.onDismissed()
    }

    companion object {

        fun newInstance(actions: List<Action>,
                        listener: MaterialBottomDialogListener): MaterialBottomDialogFragment {
            return MaterialBottomDialogFragment().apply {
                this.actions = actions
                this.listener = listener
            }
        }

    }

}
