package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.iconImageResId
import com.judopay.ui.common.isExpired
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel
import kotlinx.android.synthetic.main.payment_card_view.view.*

class PaymentCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    init {
        inflate(R.layout.payment_card_view, true)
    }

    var model = PaymentCardViewModel()
        set(value) {
            field = value
            update()
        }

    private fun update() {
        cardNameTextView.text = model.name
        cardNumberMaskTextView.text = resources.getString(R.string.mask, model.maskedNumber)
        expireDateTextView.text = model.expireDate

        val image = model.cardNetwork.iconImageResId
        if (image > 0) networkIconImageView.setImageResource(image)

        if (isExpired(model.expireDate)) {
            isExpiredTextView.visibility = View.VISIBLE
            expireDateTextView.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
            paymentCardViewContainer.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.greyish
                )
            )
        } else {
            isExpiredTextView.visibility = View.GONE
            expireDateTextView.setTextColor(ContextCompat.getColor(context, R.color.brown_grey))
            paymentCardViewContainer.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
    }
}
