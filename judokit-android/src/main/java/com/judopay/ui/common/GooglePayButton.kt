package com.judopay.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.judopay.R
import com.judopay.inflate
import kotlinx.android.synthetic.main.google_pay_button.view.*

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

    init {
        inflate(R.layout.google_pay_button, true)

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

        blackButton.visibility = visibility(GooglePayButtonStyle.BLACK)
        blackBuyWithButton.visibility = visibility(GooglePayButtonStyle.BLACK_BUY_WITH)

        whiteButton.visibility = visibility(GooglePayButtonStyle.WHITE)
        whiteButtonNoShadow.visibility = visibility(GooglePayButtonStyle.WHITE_NO_SHADOW)

        whiteBuyWithButton.visibility = visibility(GooglePayButtonStyle.WHITE_BUY_WITH)
        whiteBuyWithButtonNoShadow.visibility =
            visibility(GooglePayButtonStyle.WHITE_BUY_WITH_NO_SHADOW)
    }
}
