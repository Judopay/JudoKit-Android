package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.model.RecommendationPaymentMethod

// Todo: Update comment.
/**
 * Represents the data needed to perform the encryption card transaction with the judo API.
 * Use the [EncryptCardRequest.Builder] for object construction.
 *
 *
 * When creating a [EncryptCardRequest] the [EncryptCardRequest.judoId]
 * must be provided.
 */
class RecommendationRequest private constructor(
    var paymentMethod: RecommendationPaymentMethod?
) {
    class Builder {
        private var paymentMethod: RecommendationPaymentMethod? = null

        fun setPaymentMethod(paymentMethod: RecommendationPaymentMethod?) = apply { this.paymentMethod = paymentMethod }

        fun build(): RecommendationRequest {
            val myPaymentMethod = requireNotNull(paymentMethod)
            return RecommendationRequest(myPaymentMethod)
        }
    }
}
