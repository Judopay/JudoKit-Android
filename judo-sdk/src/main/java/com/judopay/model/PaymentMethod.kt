package com.judopay.model

import android.os.Parcelable
import com.judopay.R
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PaymentMethod : Parcelable {
    CARD,
    AMAZON_PAY,
    IDEAL,
    GOOGLE_PAY
}

internal val PaymentMethod.icon
    get() = when (this) {
        PaymentMethod.CARD -> R.drawable.ic_cards
        PaymentMethod.AMAZON_PAY -> R.drawable.ic_amazonpay
        PaymentMethod.GOOGLE_PAY -> R.drawable.ic_google_pay
        PaymentMethod.IDEAL -> R.drawable.ic_ideal
    }

internal val PaymentMethod.text
    get() = when (this) {
        PaymentMethod.CARD -> R.string.cards
        PaymentMethod.AMAZON_PAY -> R.string.amazon_pay
        PaymentMethod.GOOGLE_PAY -> R.string.empty
        PaymentMethod.IDEAL -> R.string.ideal_payment
    }