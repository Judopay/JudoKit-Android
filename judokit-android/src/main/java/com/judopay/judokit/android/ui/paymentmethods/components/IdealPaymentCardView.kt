package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.inflate
import com.judopay.judokit.android.ui.paymentmethods.model.IdealPaymentCardViewModel
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
        bankNameTextView.text = model.idealBank.title
        bankLogoImageView.setImageResource(model.idealBank.drawableResId)
    }
}
