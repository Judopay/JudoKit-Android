package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.judopay.R
import com.judopay.inflate
import kotlinx.android.synthetic.main.payment_card_view.view.*

data class PaymentCardViewModel(
        val name: String,
        val maskedNumber: String,
        val expireDate: String
)

class PaymentCardView : CardView {

    var model = PaymentCardViewModel("Card for online shopping",
            "••••    ••••    ••••    1122",
            "11/22")
        set(value) {
            field = value
            update()
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        inflate(R.layout.payment_card_view, true)
        update()
    }

    private fun update() {
        cardNameTextView.text = model.name
        cardNumberMaskTextView.text = model.maskedNumber
        expireDateTextView.text = model.expireDate
    }

}
