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

class PaymentCallToActionView : FrameLayout {

    var model = PaymentCallToActionViewModel("$15.33",
            PaymentButtonType.PLAIN,
            false)
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
        inflate(R.layout.payment_call_to_action_view, true)
        update()
    }

    private fun update() {
        amountTextView.text = model.amount
        payButton.isEnabled = model.isButtonEnabled
    }

}
