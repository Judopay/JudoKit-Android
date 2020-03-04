package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.judopay.R
import com.judopay.inflate
import com.judopay.ui.paymentmethods.model.*
import kotlinx.android.synthetic.main.payment_methods_header_view.view.*

class PaymentMethodsHeaderViewModel(
        val cardModel: CardViewModel = NoPaymentMethodSelectedViewModel(),
        val callToActionModel: PaymentCallToActionViewModel = PaymentCallToActionViewModel()
)

class PaymentMethodsHeaderView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.payment_methods_header_view, true)
    }

    var model = PaymentMethodsHeaderViewModel()
        set(value) {
            field = value
            update()
        }

    private fun update() {
        updatePreviewHeader()
        paymentCallToActionView.model = model.callToActionModel
    }

    private fun updatePreviewHeader() {
        val cardModel = model.cardModel

        // TODO: temporary

        viewAnimator.visibility = View.GONE
        placeholderBackgroundImageView.visibility = View.GONE
        noPaymentMethodSelectedView.visibility = View.GONE

        when (cardModel) {
            is NoPaymentMethodSelectedViewModel -> {
                placeholderBackgroundImageView.visibility = View.VISIBLE
                noPaymentMethodSelectedView.visibility = View.VISIBLE
            }
            is PaymentCardViewModel -> {
                viewAnimator.visibility = View.VISIBLE
                paymentCardView.model = cardModel
                while (viewAnimator.displayedChild != 0) {
                    viewAnimator.showNext()
                }
            }
            is GooglePayCardViewModel -> {
                viewAnimator.visibility = View.VISIBLE
                while (viewAnimator.displayedChild != 1) {
                    viewAnimator.showNext()
                }
            }
            is IdealPaymentCardViewModel -> {
                viewAnimator.visibility = View.VISIBLE
                while (viewAnimator.displayedChild != 2) {
                    viewAnimator.showNext()
                }
            }
        }
    }

}