package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.judopay.R
import com.judopay.inflate
import com.judopay.ui.paymentmethods.adapter.model.bankResId
import com.judopay.ui.paymentmethods.adapter.model.drawableResId
import com.judopay.ui.paymentmethods.model.IdealPaymentCardViewModel
import kotlinx.android.synthetic.main.ideal_payment_card_view.view.*

class IdealPaymentCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    init {
        inflate(R.layout.ideal_payment_card_view, true)
    }

    var model = IdealPaymentCardViewModel()
        set(value) {
            field = value
            update()
        }

    private fun update() {
        bankNameTextView.text = resources.getString(model.idealBank.bankResId())
        bankLogoImageView.setImageResource(model.idealBank.drawableResId())
    }
}
