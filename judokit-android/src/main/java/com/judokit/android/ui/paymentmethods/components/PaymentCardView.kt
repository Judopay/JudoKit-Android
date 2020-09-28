package com.judokit.android.ui.paymentmethods.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.judokit.android.R
import com.judokit.android.api.model.response.CardDate
import com.judokit.android.inflate
import com.judokit.android.model.lightIconImageResId
import com.judokit.android.ui.editcard.drawableRes
import com.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import kotlinx.android.synthetic.main.payment_card_view.view.cardNameTextView
import kotlinx.android.synthetic.main.payment_card_view.view.cardNumberMaskTextView
import kotlinx.android.synthetic.main.payment_card_view.view.expireDateTextView
import kotlinx.android.synthetic.main.payment_card_view.view.isExpiredTextView
import kotlinx.android.synthetic.main.payment_card_view.view.networkIconImageView
import kotlinx.android.synthetic.main.payment_card_view.view.paymentCardViewContainer

private const val CARD_MASK ="•••• •••• ••••"

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

    @SuppressLint("SetTextI18n")
    private fun update() {
        cardNameTextView.text = model.name
        cardNumberMaskTextView.text = "$CARD_MASK ${model.maskedNumber}"
        expireDateTextView.text = model.expireDate

        val image = model.cardNetwork.lightIconImageResId
        if (image > 0) {
            networkIconImageView.setImageResource(image)
        } else {
            networkIconImageView.setImageDrawable(null)
        }

        val date = CardDate(model.expireDate)
        if (date.isAfterToday) {
            isExpiredTextView.visibility = View.GONE
            expireDateTextView.setTextColor(ContextCompat.getColor(context, R.color.white_opaque))
            paymentCardViewContainer.background = model.pattern.drawableRes(context)
        } else {
            isExpiredTextView.visibility = View.VISIBLE
            expireDateTextView.setTextColor(ContextCompat.getColor(context, R.color.tomato_red))
            paymentCardViewContainer.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.greyish
                )
            )
        }
    }
}
