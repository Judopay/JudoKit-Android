package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.PaymentMethod
import com.judopay.ui.paymentmethods.model.CardAnimationType
import com.judopay.ui.paymentmethods.model.CardViewModel
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel
import com.judopay.ui.paymentmethods.model.inAnimation
import com.judopay.ui.paymentmethods.model.outAnimation
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
            previousCard = field.cardModel
            field = value
            update()
        }

    private var mainDisplayed = false
    private var previousSelected: CardViewModel? = null

    var paymentMethods: List<PaymentMethod> = listOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)
    /**
     * Variable used to disable animations when navigating from edit screen to payment methods screen
     * set to true in [com.judopay.ui.paymentmethods.PaymentMethodsFragment.onViewCreated]
     * set to false in [PaymentMethodsHeaderView.setAnimationType]
     */
    var fromEditMode = false
    private var previousCard: CardViewModel? = null
    private var previousPaymentMethod: PaymentMethod? = null
    private var currentPaymentMethod: PaymentMethod? = null

    private fun update() {
        updatePreviewHeader()
        paymentCallToActionView.model = model.callToActionModel
    }

    private fun updatePreviewHeader() {
        previousPaymentMethod = currentPaymentMethod

        val cardModel = model.cardModel

        viewAnimator.visibility = View.GONE
        noPaymentMethodSelectedView.hide(placeholderBackgroundImageView)
        when (cardModel) {
            is NoPaymentMethodSelectedViewModel -> {
                noPaymentMethodSelectedView.show(placeholderBackgroundImageView)
                currentPaymentMethod = null
            }
            is PaymentCardViewModel -> {
                if (mainDisplayed) {
                    cardModel.layoutId = R.id.secondaryPaymentCardView
                    secondaryPaymentCardView.model = cardModel
                } else {
                    cardModel.layoutId = R.id.cardView
                    cardView.model = cardModel
                }
                mainDisplayed = !mainDisplayed
                currentPaymentMethod = PaymentMethod.CARD
            }
            is IdealPaymentCardViewModel -> currentPaymentMethod = PaymentMethod.IDEAL
            is GooglePayCardViewModel -> currentPaymentMethod = PaymentMethod.GOOGLE_PAY
        }
        show(cardModel)
        previousSelected = cardModel
    }

    private fun show(cardModel: CardViewModel) {
        setAnimationType()
        viewAnimator.visibility = View.VISIBLE
        viewAnimator.children.forEachIndexed { index, view ->
            if (view.id == cardModel.layoutId) {
                viewAnimator.displayedChild = index
            }
        }
    }

    private fun setAnimationType() {
        val indexOfCurrent = paymentMethods.indexOf(currentPaymentMethod)
        val indexOfPrevious = paymentMethods.indexOf(previousPaymentMethod)
        when {
            previousPaymentMethod == null && !fromEditMode -> {
                viewAnimator.animateFirstView = true
                viewAnimator.inAnimation = CardAnimationType.BOTTOM_IN.inAnimation(context)
                viewAnimator.outAnimation = CardAnimationType.BOTTOM_IN.outAnimation(context)
            }
            indexOfPrevious > indexOfCurrent -> {
                viewAnimator.inAnimation = CardAnimationType.LEFT.inAnimation(context)
                viewAnimator.outAnimation = CardAnimationType.LEFT.outAnimation(context)
            }
            indexOfPrevious < indexOfCurrent -> {
                viewAnimator.inAnimation = CardAnimationType.RIGHT.inAnimation(context)
                viewAnimator.outAnimation = CardAnimationType.RIGHT.outAnimation(context)
            }
            indexOfPrevious == indexOfCurrent && previousCard != model.cardModel -> {
                if (currentPaymentMethod == PaymentMethod.CARD || currentPaymentMethod == PaymentMethod.IDEAL) {
                    viewAnimator.inAnimation = CardAnimationType.BOTTOM.inAnimation(context)
                    viewAnimator.outAnimation = CardAnimationType.BOTTOM.outAnimation(context)
                } else {
                    viewAnimator.inAnimation = CardAnimationType.NONE.inAnimation(context)
                    viewAnimator.outAnimation = CardAnimationType.NONE.outAnimation(context)
                }
            }
            indexOfPrevious == indexOfCurrent && previousCard == model.cardModel -> {
                viewAnimator.inAnimation = CardAnimationType.NONE.inAnimation(context)
                viewAnimator.outAnimation = CardAnimationType.NONE.outAnimation(context)
                fromEditMode = false
            }
        }
    }
}
