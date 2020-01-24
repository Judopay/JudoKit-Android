package com.judopay.view

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.judopay.R
import com.judopay.util.animateText
import com.judopay.validation.Validation

private const val ANIMATION_DURATION = 100L
private const val VIBRATION_DURATION = 150L

abstract class CardEntryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    lateinit var container: ConstraintLayout
    lateinit var editText: JudoEditText
    lateinit var error: TextView
    private var isValidated = false

    fun setValidation(validation: Validation) {
        val set = ConstraintSet()
        set.clone(container)
        val transition = AutoTransition().apply { duration = ANIMATION_DURATION }
        if (validation.isShowError && validation.isShowError != isValidated) {
            editText.animateText(transition, validation.isShowError)
            set.apply {
                connect(
                    editText.id,
                    ConstraintSet.TOP,
                    error.id,
                    ConstraintSet.BOTTOM
                )
                setMargin(
                    editText.id,
                    ConstraintSet.BOTTOM,
                    context.resources.getDimension(R.dimen.medium_padding).toInt()
                )
            }
            TransitionManager.beginDelayedTransition(container, transition)
            set.applyTo(container)
            error.setText(validation.error)
            error.visibility = View.VISIBLE
            editText.setTextColor(ContextCompat.getColor(context, R.color.error))
            vibrate()
        } else if (validation.isShowError != isValidated) {
            error.visibility = View.INVISIBLE
            editText.animateText(transition, validation.isShowError)
            set.apply {
                connect(
                    editText.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                setMargin(editText.id, ConstraintSet.BOTTOM, 0)
            }
            TransitionManager.beginDelayedTransition(container, transition)
            set.applyTo(container)
            error.visibility = View.INVISIBLE
            editText.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        isValidated = validation.isShowError
    }

    fun addTextChangedListener(watcher: SimpleTextWatcher?) {
        editText.addTextChangedListener(watcher)
    }

    fun setText(text: String?) {
        editText.setText(text)
    }

    open fun getText(): String? = editText.text.toString().trim { it <= ' ' }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    VIBRATION_DURATION,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(VIBRATION_DURATION)
        }
    }
}