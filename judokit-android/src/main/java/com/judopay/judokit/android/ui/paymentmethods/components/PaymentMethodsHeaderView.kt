package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.PaymentMethodsHeaderViewBinding
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.ui.paymentmethods.model.CardAnimationType
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.IdealPaymentCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.inAnimation
import com.judopay.judokit.android.ui.paymentmethods.model.outAnimation

class PaymentMethodsHeaderViewModel(
    val cardModel: CardViewModel = NoPaymentMethodSelectedViewModel(),
    val callToActionModel: PaymentCallToActionViewModel = PaymentCallToActionViewModel()
)

class PaymentMethodsHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    val binding = PaymentMethodsHeaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    var model = PaymentMethodsHeaderViewModel()
        set(value) {
            previousCard = field.cardModel
            field = value
            update()
        }

    private var mainDisplayed = true
    private var previousSelected: CardViewModel? = null

    var paymentMethods: List<PaymentMethod> = listOf(PaymentMethod.CARD, PaymentMethod.GOOGLE_PAY)

    /**
     * Variable used to disable animations when navigating from edit screen to payment methods screen
     * set to true in [com.judopay.judokit.android.ui.paymentmethods.PaymentMethodsFragment.onViewCreated]
     * set to false in [PaymentMethodsHeaderView.setAnimationType]
     */
    var fromEditMode = false
    private var previousCard: CardViewModel? = null
    private var previousPaymentMethod: PaymentMethod? = null
    private var currentPaymentMethod: PaymentMethod? = null

    private fun update() {
        updatePreviewHeader()
        binding.paymentCallToActionView.model = model.callToActionModel
    }

    private fun updatePreviewHeader() {
        previousPaymentMethod = currentPaymentMethod

        val cardModel = model.cardModel

        binding.viewAnimator.visibility = View.GONE
        binding.noPaymentMethodSelectedView.hide(binding.placeholderBackgroundImageView)
        when (cardModel) {
            is NoPaymentMethodSelectedViewModel -> {
                binding.noPaymentMethodSelectedView.show(binding.placeholderBackgroundImageView)
                currentPaymentMethod = null
            }
            is PaymentCardViewModel -> {
                if (mainDisplayed) {
                    cardModel.layoutId = R.id.secondaryPaymentCardView
                    val myCardView = binding.secondaryPaymentCardView.children.first()
                    (myCardView as PaymentCardView).model = cardModel
                } else {
                    cardModel.layoutId = R.id.cardView
                    val myCardView = binding.cardView.children.first()
                    (myCardView as PaymentCardView).model = cardModel
                }
                mainDisplayed = !mainDisplayed
                currentPaymentMethod = PaymentMethod.CARD
            }
            is IdealPaymentCardViewModel -> {
                if (mainDisplayed) {
                    cardModel.layoutId = R.id.secondaryIdealPaymentCardView
                    val myIdealPaymentCardView = binding.secondaryIdealPaymentCardView.children.first()
                    (myIdealPaymentCardView as IdealPaymentCardView).model = cardModel
                } else {
                    cardModel.layoutId = R.id.idealPaymentCardView
                    val myIdealPaymentCardView = binding.idealPaymentCardView.children.first()
                    (myIdealPaymentCardView as IdealPaymentCardView).model = cardModel
                }
                mainDisplayed = !mainDisplayed
                currentPaymentMethod = PaymentMethod.IDEAL
            }
            is GooglePayCardViewModel -> currentPaymentMethod = PaymentMethod.GOOGLE_PAY
        }
        show(cardModel)
        previousSelected = cardModel
    }

    private fun show(cardModel: CardViewModel) {
        setAnimationType()
        binding.viewAnimator.visibility = View.VISIBLE
        binding.viewAnimator.children.forEachIndexed { index, view ->
            if (view.id == cardModel.layoutId) {
                binding.viewAnimator.displayedChild = index
            }
        }
    }

    private fun setAnimationType() {
        val indexOfCurrent = paymentMethods.indexOf(currentPaymentMethod)
        val indexOfPrevious = paymentMethods.indexOf(previousPaymentMethod)
        when {
            previousPaymentMethod == null && !fromEditMode -> {
                binding.viewAnimator.animateFirstView = true
                binding.viewAnimator.inAnimation = CardAnimationType.BOTTOM_IN.inAnimation(context)
                binding.viewAnimator.outAnimation = CardAnimationType.BOTTOM_IN.outAnimation(context)
            }
            indexOfPrevious > indexOfCurrent -> {
                binding.viewAnimator.inAnimation = CardAnimationType.LEFT.inAnimation(context)
                binding.viewAnimator.outAnimation = CardAnimationType.LEFT.outAnimation(context)
            }
            indexOfPrevious < indexOfCurrent -> {
                binding.viewAnimator.inAnimation = CardAnimationType.RIGHT.inAnimation(context)
                binding.viewAnimator.outAnimation = CardAnimationType.RIGHT.outAnimation(context)
            }
            indexOfPrevious == indexOfCurrent && previousCard != model.cardModel -> {
                if (currentPaymentMethod == PaymentMethod.CARD || currentPaymentMethod == PaymentMethod.IDEAL) {
                    binding.viewAnimator.inAnimation = CardAnimationType.BOTTOM.inAnimation(context)
                    binding.viewAnimator.outAnimation = CardAnimationType.BOTTOM.outAnimation(context)
                } else {
                    binding.viewAnimator.inAnimation = CardAnimationType.NONE.inAnimation(context)
                    binding.viewAnimator.outAnimation = CardAnimationType.NONE.outAnimation(context)
                }
            }
            indexOfPrevious == indexOfCurrent && previousCard == model.cardModel -> {
                binding.viewAnimator.inAnimation = CardAnimationType.NONE.inAnimation(context)
                binding.viewAnimator.outAnimation = CardAnimationType.NONE.outAnimation(context)
                fromEditMode = false
            }
        }
    }
}
