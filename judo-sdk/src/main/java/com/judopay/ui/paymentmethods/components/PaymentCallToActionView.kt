package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.judopay.R
import com.judopay.inflate
import com.judopay.ui.common.ButtonState
import kotlinx.android.synthetic.main.payment_call_to_action_view.view.*

enum class PaymentButtonType {
    PLAIN,
    GOOGLE_PAY
}

enum class PaymentCallToActionType {
    PAY_WITH_CARD,
    PAY_WITH_GOOGLE_PAY
}

typealias PaymentCallToActionViewListener = (action: PaymentCallToActionType) -> Unit

data class PaymentCallToActionViewModel(
    val amount: String = "",
    val buttonType: PaymentButtonType = PaymentButtonType.PLAIN,
    val paymentButtonState: ButtonState = ButtonState.Disabled(R.string.pay_now)
)

class PaymentCallToActionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    var callbackListener: PaymentCallToActionViewListener? = null

    init {
        inflate(R.layout.payment_call_to_action_view, true)
        payButton.setOnClickListener(::onPaymentButtonClick)
        googlePayButton.setOnClickListener(::onPaymentButtonClick)
    }

    var model = PaymentCallToActionViewModel()
        set(value) {
            field = value
            update()
        }

    private fun update() = with(model) {
        amountTextView.text = amount
        payButton.state = paymentButtonState

        val buttonToShow = when (buttonType) {
            PaymentButtonType.PLAIN -> payButton
            PaymentButtonType.GOOGLE_PAY -> googlePayButton
        }

        if (buttonsAnimator.currentView != buttonToShow)
            buttonsAnimator.showNext()
    }

    private fun onPaymentButtonClick(view: View) {

        val actionType = when (view) {
            payButton -> PaymentCallToActionType.PAY_WITH_CARD
            googlePayButton -> PaymentCallToActionType.PAY_WITH_GOOGLE_PAY
            else -> null
        }

        actionType?.let {
            callbackListener?.invoke(it)
        }
    }
}
