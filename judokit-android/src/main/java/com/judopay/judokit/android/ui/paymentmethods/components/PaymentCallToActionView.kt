package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.PaymentCallToActionViewBinding
import com.judopay.judokit.android.ui.common.ButtonState

enum class PaymentButtonType {
    PLAIN,
    IDEAL,
    GOOGLE_PAY,
}

enum class PaymentCallToActionType {
    PAY_WITH_CARD,
    PAY_WITH_GOOGLE_PAY,
    PAY_WITH_IDEAL,
}

typealias PaymentCallToActionViewListener = (action: PaymentCallToActionType) -> Unit

data class PaymentCallToActionViewModel(
    val amount: String = "",
    val buttonType: PaymentButtonType = PaymentButtonType.PLAIN,
    val paymentButtonState: ButtonState = ButtonState.Disabled(R.string.pay_now),
    val shouldDisplayAmount: Boolean = false,
)

class PaymentCallToActionView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : FrameLayout(context, attrs, defStyle) {
        val binding = PaymentCallToActionViewBinding.inflate(LayoutInflater.from(context), this, true)
        var callbackListener: PaymentCallToActionViewListener? = null

        init {
            binding.payButton.setOnClickListener { onPaymentButtonClick() }
            binding.googlePayButton.setOnClickListener { onPaymentButtonClick() }
        }

        var model = PaymentCallToActionViewModel()
            set(value) {
                field = value
                update()
            }

        private fun update() =
            with(model) {
                binding.amountTextView.text = amount
                val visibility = if (shouldDisplayAmount) View.VISIBLE else View.GONE
                binding.youWillPayTextView.visibility = visibility
                binding.amountTextView.visibility = visibility

                when (buttonType) {
                    PaymentButtonType.PLAIN,
                    PaymentButtonType.IDEAL,
                    -> binding.payButton.state = paymentButtonState
                    PaymentButtonType.GOOGLE_PAY ->
                        binding.googlePayButton.isEnabled =
                            paymentButtonState is ButtonState.Enabled
                }

                val buttonToShow =
                    when (buttonType) {
                        PaymentButtonType.PLAIN,
                        PaymentButtonType.IDEAL,
                        -> binding.payButton
                        PaymentButtonType.GOOGLE_PAY -> binding.googlePayButton
                    }

                with(binding.buttonsAnimator) {
                    if (currentView != buttonToShow) {
                        children.forEachIndexed { index, view ->
                            if (view == buttonToShow) {
                                displayedChild = index
                            }
                        }
                    }
                }
            }

        private fun onPaymentButtonClick() {
            val actionType =
                when (model.buttonType) {
                    PaymentButtonType.PLAIN -> PaymentCallToActionType.PAY_WITH_CARD
                    PaymentButtonType.GOOGLE_PAY -> PaymentCallToActionType.PAY_WITH_GOOGLE_PAY
                    PaymentButtonType.IDEAL -> PaymentCallToActionType.PAY_WITH_IDEAL
                }

            callbackListener?.invoke(actionType)
        }
    }
