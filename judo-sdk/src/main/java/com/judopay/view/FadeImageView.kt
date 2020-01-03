package com.judopay.view

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView

private const val KEY_ALPHA = "alpha"
private const val KEY_IMAGE_TYPE = "imageType"

abstract class FadeImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private var imageView: AppCompatImageView? = null
    private var imageType = 0

    init {
        setImageType(0, false)
    }

    @DrawableRes
    protected abstract fun getImageResource(type: Int): Int

    public override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
            putFloat(KEY_ALPHA, alpha)
            putInt(KEY_IMAGE_TYPE, imageType)
        }
    }

    public override fun onRestoreInstanceState(bundle: Parcelable) {
        if (bundle is Bundle) {
            val superState = bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)

            alpha = bundle.getFloat(KEY_ALPHA)
            imageType = bundle.getInt(KEY_IMAGE_TYPE)

            setImageType(imageType, false)

            super.onRestoreInstanceState(superState)
        } else {
            super.onRestoreInstanceState(bundle)
        }
    }

    fun setImageType(imageType: Int, animated: Boolean) {
        this.imageType = imageType
        this.visibility = View.VISIBLE
        val imageResId = getImageResource(imageType)

        if (imageView == null) {
            imageView = AppCompatImageView(context)
            addView(imageView)
        }

        imageView?.apply {
            if (tag != imageResId) {
                toggleImageVisibility(imageResId, animated)
            }
            tag = imageResId
        }
    }

    private fun toggleImageVisibility(@DrawableRes imageResId: Int, animated: Boolean) {
        imageView?.let {
            val hasImage = imageResId != 0
            val to = if (hasImage) 1f else 0f
            val from = if (hasImage) 0f else 1f

            it.alpha = if (animated) from else to
            it.setImageResource(imageResId)

            if (animated) {
                ObjectAnimator.ofFloat(it, View.ALPHA, from, to).apply { duration = 300 }.start()
            }
        }
    }
}