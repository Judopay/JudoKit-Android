package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import com.judopay.R
import com.judopay.animateWithAlpha
import com.judopay.animateWithTranslation
import com.judopay.inflate
import com.judopay.ui.paymentmethods.model.CardViewModel
import com.judopay.ui.paymentmethods.model.CardViewType
import kotlinx.android.synthetic.main.payment_methods_header_view.view.noPaymentMethodSelectedView

private const val TRANSLATE_120 = 120f

data class NoPaymentMethodSelectedViewModel(
    override val type: CardViewType = CardViewType.PLACEHOLDER,
    override var layoutId: Int = R.id.noPaymentMethodSelectedView
) : CardViewModel

class NoPaymentMethodSelectedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.no_payment_method_selected_view, true)
    }

    fun show(placeholderImage: ImageView) {
        placeholderImage.animateWithAlpha(alpha = 1.0f)
        noPaymentMethodSelectedView.animateWithTranslation(translationY = 0.0f, alpha = 1.0f)
    }

    fun hide(placeholderImage: ImageView) {
        placeholderImage.animateWithAlpha(alpha = 0.0f)
        noPaymentMethodSelectedView.animateWithTranslation(
            translationY = TRANSLATE_120,
            alpha = 0.0f
        )
    }
}
