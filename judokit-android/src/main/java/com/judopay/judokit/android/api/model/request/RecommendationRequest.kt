package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.model.RecommendationPaymentMethod

/**
 * Represents the data needed to perform the recommendation request with the Recommendation API.
 * Use the [RecommendationRequest.Builder] for object construction.
 *
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
