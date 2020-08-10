package com.judokit.android.ui.paymentmethods.model

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.judokit.android.R

enum class CardAnimationType {
    NONE,
    LEFT,
    RIGHT,
    BOTTOM,
    BOTTOM_IN
}

internal fun CardAnimationType.inAnimation(context: Context): Animation? = when (this) {
    CardAnimationType.NONE -> null
    CardAnimationType.BOTTOM_IN -> AnimationUtils.loadAnimation(context, R.anim.fade_slide_in_bottom)
    CardAnimationType.LEFT -> AnimationUtils.loadAnimation(context, R.anim.fade_slide_in_left)
    CardAnimationType.RIGHT -> AnimationUtils.loadAnimation(context, R.anim.fade_slide_in_right)
    CardAnimationType.BOTTOM -> AnimationUtils.loadAnimation(context, R.anim.fade_slide_in_bottom)
}

internal fun CardAnimationType.outAnimation(context: Context): Animation? = when (this) {
    CardAnimationType.NONE -> null
    CardAnimationType.BOTTOM_IN -> null
    CardAnimationType.LEFT -> AnimationUtils.loadAnimation(context, R.anim.fade_slide_out_right)
    CardAnimationType.RIGHT -> AnimationUtils.loadAnimation(context, R.anim.fade_slide_out_left)
    CardAnimationType.BOTTOM -> AnimationUtils.loadAnimation(context, R.anim.fade_slide_out_bottom)
}
