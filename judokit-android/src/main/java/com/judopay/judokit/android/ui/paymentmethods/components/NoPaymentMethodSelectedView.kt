package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.animateWithAlpha
import com.judopay.judokit.android.animateWithTranslation
import com.judopay.judokit.android.databinding.NoPaymentMethodSelectedViewBinding
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewType

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
    private val binding = NoPaymentMethodSelectedViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun show(placeholderImage: ImageView) {
        placeholderImage.apply {
            visibility = View.VISIBLE
            animateWithAlpha(alpha = 1.0f)
        }
        binding.root.apply {
            visibility = View.VISIBLE
            animateWithTranslation(translationY = 0.0f, alpha = 1.0f)
        }
    }

    fun hide(placeholderImage: ImageView) {
        placeholderImage.apply {
            visibility = View.GONE
            animateWithAlpha(alpha = 0.0f)
        }
        binding.root.apply {
            visibility = View.GONE
            animateWithTranslation(
                translationY = TRANSLATE_120,
                alpha = 0.0f
            )
        }
    }
}
