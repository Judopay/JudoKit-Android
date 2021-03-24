package com.judopay.judokit.android.ui.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.judopay.judokit.android.R

sealed class ButtonState {
    data class Enabled(@StringRes val text: Int, val amount: String? = null) : ButtonState()
    data class Disabled(@StringRes val text: Int, val amount: String? = null) : ButtonState()
    object Loading : ButtonState()
}

class ProgressButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : MaterialButton(context, attrs, defStyle), Drawable.Callback {

    var state: ButtonState = ButtonState.Enabled(R.string.pay_now)
        set(value) {
            field = value
            updateState()
        }

    private val progressDrawable: CircularProgressDrawable by lazy {
        return@lazy CircularProgressDrawable(context).apply {
            setStyle(CircularProgressDrawable.LARGE)
            setColorSchemeColors(currentTextColor)

            // bounds definition is required to show drawable correctly
            val size = (centerRadius + strokeWidth).toInt() * 2
            setBounds(0, 0, size, size)
        }
    }
    private val progressSpan: SpannableString by lazy {
        // create a drawable span using our progress drawable
        val drawableSpan = object : DynamicDrawableSpan() {
            override fun getDrawable() = progressDrawable
        }

        return@lazy SpannableString(" ").apply {
            setSpan(drawableSpan, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun updateState() {
        val text = when (val myState = state) {
            is ButtonState.Enabled -> resources.getString(myState.text, myState.amount ?: "")
            is ButtonState.Disabled -> resources.getString(myState.text, myState.amount ?: "")
            else -> resources.getString(R.string.empty)
        }

        super.setText(text)

        isEnabled = state !is ButtonState.Disabled
        isClickable = state !is ButtonState.Loading

        val isAnimating = state is ButtonState.Loading
        setIsAnimatingText(isAnimating)
    }

    private fun setIsAnimatingText(isAnimating: Boolean) = if (isAnimating) {
        super.setText(progressSpan)
        progressDrawable.callback = this
        progressDrawable.start()
    } else {
        progressDrawable.stop()
        progressDrawable.callback = null
    }

    // Drawable.Callback
    override fun unscheduleDrawable(who: Drawable, what: Runnable) {}

    override fun invalidateDrawable(who: Drawable) {
        this.invalidate()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}

    // Lifecycle
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressDrawable.stop()
        progressDrawable.callback = null
    }
}
