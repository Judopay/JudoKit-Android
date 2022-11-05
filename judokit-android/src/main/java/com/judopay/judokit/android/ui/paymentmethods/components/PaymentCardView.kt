package com.judopay.judokit.android.ui.paymentmethods.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.databinding.PaymentCardViewBinding
import com.judopay.judokit.android.model.lightIconImageResId
import com.judopay.judokit.android.ui.editcard.drawableRes
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel

private const val CARD_MASK = "•••• •••• ••••"

class PaymentCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {
    private val binding = PaymentCardViewBinding.inflate(LayoutInflater.from(context), this, true)

    var model = PaymentCardViewModel()
        set(value) {
            field = value
            update()
        }

    @SuppressLint("SetTextI18n")
    private fun update() {
        binding.cardNameTextView.text = model.name
        binding.cardNumberMaskTextView.text = "$CARD_MASK ${model.maskedNumber}"
        binding.expireDateTextView.text = model.expireDate

        val image = model.cardNetwork.lightIconImageResId
        if (image > 0) {
            binding.networkIconImageView.setImageResource(image)
        } else {
            binding.networkIconImageView.setImageDrawable(null)
        }

        val date = CardDate(model.expireDate)
        if (date.isAfterToday) {
            binding.isExpiredTextView.visibility = View.GONE
            binding.expireDateTextView.setTextColor(ContextCompat.getColor(context, R.color.white_opaque))
            binding.paymentCardViewContainer.background = model.pattern.drawableRes(context)
        } else {
            binding.isExpiredTextView.visibility = View.VISIBLE
            binding.expireDateTextView.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
            binding.paymentCardViewContainer.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.greyish
                )
            )
        }
    }
}
