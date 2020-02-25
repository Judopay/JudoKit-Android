package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.judopay.R
import com.judopay.inflate
import kotlinx.android.synthetic.main.payment_call_to_action_view.view.*

enum class PaymentButtonType {
    PLAIN,
    GOOGLE_PAY
}

data class PaymentCallToActionViewModel(
        val amount: String,
        val buttonType: PaymentButtonType,
        val isButtonEnabled: Boolean
)

class PaymentCallToActionView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.payment_call_to_action_view, true)
    }

    var model = PaymentCallToActionViewModel("$15.33",
            PaymentButtonType.PLAIN,
            true)
        set(value) {
            field = value
            update()
        }

    private fun update() {
        amountTextView.text = model.amount
        payButton.isEnabled = model.isButtonEnabled
    }

}
