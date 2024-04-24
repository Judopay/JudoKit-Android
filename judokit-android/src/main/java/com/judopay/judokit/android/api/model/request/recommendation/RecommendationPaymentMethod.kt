package com.judopay.judokit.android.api.model.request.recommendation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendationPaymentMethod(
    val paymentMethodCipher: PaymentMethodCipher? = null,
) : Parcelable
