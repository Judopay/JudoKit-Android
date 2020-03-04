package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.judopay.R
import com.judopay.inflate
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
        cardNumberMaskTextView.text = model.maskedNumber
        expireDateTextView.text = model.expireDate
    }

}
