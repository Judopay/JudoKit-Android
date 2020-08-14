package com.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import com.judokit.android.R
import com.judokit.android.inflate
import com.judokit.android.ui.common.ButtonState
import kotlinx.android.synthetic.main.payment_call_to_action_view.view.*
import kotlinx.android.synthetic.main.pbba_button.view.*

enum class PaymentButtonType {
    PLAIN,
    IDEAL,
    GOOGLE_PAY,
    PAY_BY_BANK
}

enum class PaymentCallToActionType {
    PAY_WITH_CARD,
    PAY_WITH_GOOGLE_PAY,
    PAY_WITH_IDEAL,
    PAY_WITH_PAY_BY_BANK
}

typealias PaymentCallToActionViewListener = (action: PaymentCallToActionType) -> Unit

data class PaymentCallToActionViewModel(
    val amount: String = "",
    val buttonType: PaymentButtonType = PaymentButtonType.PLAIN,
    val paymentButtonState: ButtonState = ButtonState.Disabled(R.string.pay_now),
    val shouldDisplayAmount: Boolean = false
)

class PaymentCallToActionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    var callbackListener: PaymentCallToActionViewListener? = null

    init {
        inflate(R.layout.payment_call_to_action_view, true)
        payButton.setOnClickListener { onPaymentButtonClick() }
        googlePayButton.setOnClickListener { onPaymentButtonClick() }
        payByBankButton.setOnClickListener { onPaymentButtonClick() }
    }

    var model = PaymentCallToActionViewModel()
        set(value) {
            field = value
            update()
        }

    private fun update() = with(model) {
        amountTextView.text = amount
        val visibility = if (shouldDisplayAmount) View.VISIBLE else View.GONE
        youWillPayTextView.visibility = visibility
        amountTextView.visibility = visibility

        when (buttonType) {
            PaymentButtonType.PLAIN,
            PaymentButtonType.IDEAL -> payButton.state = paymentButtonState
            PaymentButtonType.GOOGLE_PAY ->
                googlePayButton.isEnabled =
                    paymentButtonState is ButtonState.Enabled
            PaymentButtonType.PAY_BY_BANK ->
                payByBankButton.isEnabled =
                    paymentButtonState is ButtonState.Enabled
        }

        val buttonToShow = when (buttonType) {
            PaymentButtonType.PLAIN,
            PaymentButtonType.IDEAL -> payButton
            PaymentButtonType.GOOGLE_PAY -> googlePayButton
            PaymentButtonType.PAY_BY_BANK -> payByBankButton
        }

        with(buttonsAnimator) {
            if (currentView != buttonToShow)
                children.forEachIndexed { index, view ->
                    if (view == buttonToShow) {
                        displayedChild = index
                    }
                }
        }
    }

    private fun onPaymentButtonClick() {
        val actionType = when (model.buttonType) {
            PaymentButtonType.PLAIN -> PaymentCallToActionType.PAY_WITH_CARD
            PaymentButtonType.GOOGLE_PAY -> PaymentCallToActionType.PAY_WITH_GOOGLE_PAY
            PaymentButtonType.IDEAL -> PaymentCallToActionType.PAY_WITH_IDEAL
            PaymentButtonType.PAY_BY_BANK -> PaymentCallToActionType.PAY_WITH_PAY_BY_BANK
        }

        callbackListener?.invoke(actionType)
    }
}
