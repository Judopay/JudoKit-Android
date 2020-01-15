package com.judopay.util

import android.animation.ObjectAnimator
import android.widget.EditText
import androidx.transition.Transition
import androidx.transition.TransitionSet

private const val TEXT_SIZE_VALID = 16f
private const val TEXT_SIZE_INVALID = 14f
private const val TEXT_SIZE_ANIMATION_DURATION = 100L

fun EditText.animateText(
    transition: TransitionSet,
    valid: Boolean
) {
    transition.addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
        }

        override fun onTransitionResume(transition: Transition) {
        }

        override fun onTransitionPause(transition: Transition) {
        }

        override fun onTransitionCancel(transition: Transition) {
        }

        override fun onTransitionStart(transition: Transition) {
            if (valid) {
                ObjectAnimator.ofFloat(this@animateText, "textSize", TEXT_SIZE_VALID, TEXT_SIZE_INVALID)
            } else {
                ObjectAnimator.ofFloat(this@animateText, "textSize", TEXT_SIZE_INVALID, TEXT_SIZE_VALID)
            }.apply {
                duration = TEXT_SIZE_ANIMATION_DURATION
                start()
            }
        }
    })
}