package com.judopay.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PaymentMethod : Parcelable {
    CARD,
    IDEAL,
    GOOGLE_PAY
}
