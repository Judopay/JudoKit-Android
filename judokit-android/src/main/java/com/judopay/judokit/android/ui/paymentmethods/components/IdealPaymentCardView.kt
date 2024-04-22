package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.judopay.judokit.android.databinding.IdealPaymentCardViewBinding
import com.judopay.judokit.android.ui.paymentmethods.model.IdealPaymentCardViewModel

class IdealPaymentCardView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : CardView(context, attrs, defStyle) {
        private val binding = IdealPaymentCardViewBinding.inflate(LayoutInflater.from(context), this, true)

        var model = IdealPaymentCardViewModel()
            set(value) {
                field = value
                update()
            }

        private fun update() {
            binding.bankNameTextView.text = model.idealBank.title
            binding.bankLogoImageView.setImageResource(model.idealBank.drawableResId)
        }
    }
