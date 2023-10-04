package com.judopay.judokit.android.model

import android.os.Parcelable
import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentButtonType
import kotlinx.parcelize.Parcelize

/**
 * A set of available payment methods to use in payment methods screen.
 * If no payment method is specified, then only card payment will be available.
 */
@Parcelize
enum class PaymentMethod : Parcelable {
    /**
     * Adds card payment method.
     */
    CARD,

    /**
     * Adds GooglePay payment method.
     */
    GOOGLE_PAY,

    /**
     * Adds iDEAL payment method.
     * [Currency] must also be set to EUR to use this payment method.
     */
    IDEAL
}

internal val PaymentMethod.icon
    get() = when (this) {
        PaymentMethod.CARD -> R.drawable.ic_cards
        PaymentMethod.GOOGLE_PAY -> R.drawable.ic_google_pay
        PaymentMethod.IDEAL -> R.drawable.ic_ideal
    }

internal val PaymentMethod.text
    get() = when (this) {
        PaymentMethod.CARD -> R.string.cards
        PaymentMethod.IDEAL -> R.string.ideal_payment
        PaymentMethod.GOOGLE_PAY -> R.string.empty
    }

internal val PaymentMethod.paymentButtonType: PaymentButtonType
    get() = when (this) {
        PaymentMethod.IDEAL -> PaymentButtonType.IDEAL
        PaymentMethod.GOOGLE_PAY -> PaymentButtonType.GOOGLE_PAY
        else -> PaymentButtonType.PLAIN
    }
