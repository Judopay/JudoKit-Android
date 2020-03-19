package com.judopay.model

import android.os.Parcelable
import com.judopay.R
import com.judopay.ui.paymentmethods.components.PaymentButtonType
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PaymentMethod : Parcelable {
    CARD,
    GOOGLE_PAY,
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
        PaymentMethod.GOOGLE_PAY -> R.string.empty
        PaymentMethod.IDEAL -> R.string.ideal_payment
    }

internal val PaymentMethod.paymentButtonType: PaymentButtonType
    get() = when (this) {
        PaymentMethod.GOOGLE_PAY -> PaymentButtonType.GOOGLE_PAY
        else -> PaymentButtonType.PLAIN
    }
