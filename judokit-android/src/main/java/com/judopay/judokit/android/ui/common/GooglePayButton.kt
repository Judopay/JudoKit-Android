package com.judopay.judokit.android.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.GooglePayButtonBinding

enum class GooglePayButtonStyle {
    BLACK,
    WHITE,
    BLACK_BUY_WITH,
    WHITE_NO_SHADOW,
    WHITE_BUY_WITH,
    WHITE_BUY_WITH_NO_SHADOW
}

class GooglePayButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    var style: GooglePayButtonStyle = GooglePayButtonStyle.BLACK
        set(value) {
            field = value
            update()
        }
    private val binding = GooglePayButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GooglePayButton)

        for (i in 0 until typedArray.indexCount) {
            when (val at = typedArray.getIndex(i)) {
                R.styleable.GooglePayButton_style -> {
                    val value = typedArray.getInt(at, 0)
                    style = GooglePayButtonStyle.values().firstOrNull { it.ordinal == value }
                        ?: GooglePayButtonStyle.BLACK
                }
            }
        }

        typedArray.recycle()
    }

    private fun update() {

        fun visibility(style: GooglePayButtonStyle): Int {
            return if (style == this.style) View.VISIBLE else View.GONE
        }

        binding.blackButton.root.visibility = visibility(GooglePayButtonStyle.BLACK)
        binding.blackBuyWithButton.root.visibility = visibility(GooglePayButtonStyle.BLACK_BUY_WITH)

        binding.whiteButton.root.visibility = visibility(GooglePayButtonStyle.WHITE)
        binding.whiteButtonNoShadow.root.visibility = visibility(GooglePayButtonStyle.WHITE_NO_SHADOW)

        binding.whiteBuyWithButton.root.visibility = visibility(GooglePayButtonStyle.WHITE_BUY_WITH)
        binding.whiteBuyWithButtonNoShadow.root.visibility = visibility(GooglePayButtonStyle.WHITE_BUY_WITH_NO_SHADOW)
    }
}
