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
    private var imageResId = 0
    private var isFadedIn = false

    init {
        setImageType(0, false)
    }

    @DrawableRes
    protected abstract fun getImageResource(type: Int): Int

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState())
        bundle.putFloat(KEY_ALPHA, alpha)
        bundle.putInt(KEY_IMAGE_TYPE, imageType)
        return bundle
    }

    public override fun onRestoreInstanceState(bundle: Parcelable) {
        if (bundle is Bundle) {
            val superState =
                bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)
            alpha = bundle.getFloat(KEY_ALPHA)
            imageType = bundle.getInt(KEY_IMAGE_TYPE)
            setImageType(imageType, false)
            super.onRestoreInstanceState(superState)
        } else {
            super.onRestoreInstanceState(bundle)
        }
    }

    fun setImageType(imageType: Int, animate: Boolean) {
        this.imageType = imageType
        this.visibility = View.VISIBLE

        if (imageView == null) {
            this.imageView = AppCompatImageView(context)
            this.imageView?.setImageResource(getImageResource(imageType))
            addView(this.imageView)
        }
        val imageResId = getImageResource(imageType)
        if (this.imageResId != imageResId) {
            if (animate) {
                flipImages(imageResId)
            } else {
                showImage(imageResId)
            }
        }
    }

    private fun showImage(imageResId: Int) {
        this.imageResId = imageResId
        imageView?.setImageResource(imageResId)
    }

    private fun flipImages(imageResId: Int) {
        if (isFadedIn) {
            val fadeOut = ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f, 0f)
            fadeOut.duration = 300
            fadeOut.start()
            this.imageResId = imageResId
        } else {
            this.imageResId = imageResId
            imageView?.setImageResource(imageResId)
            val fadeOut = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, 1f)
            fadeOut.duration = 300
            fadeOut.start()
        }
        isFadedIn = !isFadedIn
    }
}