package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendationPaymentMethod(
    val paymentMethodCipher: PaymentMethodCipher? = null
) : Parcelable
