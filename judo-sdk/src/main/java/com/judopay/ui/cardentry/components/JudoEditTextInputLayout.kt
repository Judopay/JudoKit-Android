package com.judopay.ui.cardentry.components

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.judopay.R
import com.judopay.inflate
import com.judopay.subViewsWithType
import kotlinx.android.synthetic.main.judo_edit_text_input_layout.view.*

private const val TEXT_SIZE_VALID = 16f
private const val TEXT_SIZE_INVALID = 14f
private const val TEXT_SIZE_ANIMATION_DURATION = 100L

class JudoEditTextInputLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.judo_edit_text_input_layout, true)
    }

    var isErrorEnabled: Boolean = false
        set(value) {
            val oldState = field
            field = value
            if (oldState != value) {
                updateErrorView()
            }
        }

    var error: String? = null
        set(value) {
            field = value
            errorTextView.text = value
        }

    @DrawableRes
    var accessoryImage: Int? = null
        set(value) {
            field = if (value ?: 0 > 0) value else null
            updateAccessoryViewState()
        }

    var editText: EditText? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        editText = subViewsWithType(AppCompatEditText::class.java).firstOrNull()

        editText?.let {
            removeView(it)
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            containerLayout.addView(it, layoutParams)
        }
    }

    private fun updateErrorView() {
        if (isAttachedToWindow) {
            errorTextView.visibility = if (isErrorEnabled) View.VISIBLE else View.GONE
        }

        val textColor = if (isErrorEnabled) R.color.tomato_red else R.color.black
        val fromToValues = if (isErrorEnabled) Pair(TEXT_SIZE_VALID, TEXT_SIZE_INVALID) else Pair(TEXT_SIZE_INVALID, TEXT_SIZE_VALID)

        editText?.let { editTextView ->
            ObjectAnimator.ofFloat(editTextView, "textSize", fromToValues.first, fromToValues.second).apply {
                doOnEnd {
                    editTextView.textSize = fromToValues.second
                    editTextView.setTextColor(ContextCompat.getColor(context, textColor))
                }
                duration = TEXT_SIZE_ANIMATION_DURATION
            }.start()
        }
    }

    private fun updateAccessoryViewState() {
        accessoryImage?.let {
            accessoryImageView.setImageResource(it)
            accessoryImageView.visibility = View.VISIBLE
            return
        }

        accessoryImageView.setImageDrawable(null)
        accessoryImageView.visibility = View.GONE
    }
}