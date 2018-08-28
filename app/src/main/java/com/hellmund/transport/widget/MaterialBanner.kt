package com.hellmund.transport.widget

import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import com.hellmund.transport.R
import kotlinx.android.synthetic.main.banner.view.*

class MaterialBanner(private val root: ViewGroup) {

    private val context = root.context
    private val bannerView = View.inflate(root.context, R.layout.banner, null)

    fun setIcon(resId: Int): MaterialBanner {
        bannerView.iconImageView.setImageResource(resId)
        return this
    }

    fun setTitle(title: String): MaterialBanner {
        bannerView.titleTextView.apply {
            text = title
            visibility = View.VISIBLE
        }
        return this
    }

    fun setMessage(resId: Int) = setMessage(context.getString(resId))

    fun setMessage(message: String): MaterialBanner {
        bannerView.messageTextView.text = message
        return this
    }

    fun setNegativeButton(resId: Int, callback: (MaterialBanner) -> Unit): MaterialBanner {
        bannerView.negativeButton.apply {
            text = context.getString(resId)
            setOnClickListener {
                callback(this@MaterialBanner)
                dismiss()
            }
        }
        return this
    }

    fun setPositiveButton(resId: Int, callback: (MaterialBanner) -> Unit): MaterialBanner {
        bannerView.positiveButton.apply {
            text = context.getString(resId)
            setOnClickListener {
                callback(this@MaterialBanner)
                dismiss()
            }
        }
        return this
    }

    fun show() {
        TransitionManager.beginDelayedTransition(root)
        root.addView(bannerView, 0)
    }

    fun dismiss() {
        TransitionManager.beginDelayedTransition(root)
        root.removeView(bannerView)
    }

    companion object {

        fun make(root: ViewGroup) = MaterialBanner(root)

    }

}