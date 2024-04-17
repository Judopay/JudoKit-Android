package com.judopay.judokit.android.api.model.request.recommendation

/**
 * Represents the data needed to perform the recommendation request with the Recommendation API.
 * Use the [RecommendationRequest.Builder] for object construction.
 *
 */
class RecommendationRequest private constructor(
    var paymentMethod: RecommendationPaymentMethod?,
) {
    class Builder {
        private var paymentMethod: RecommendationPaymentMethod? = null

        fun setPaymentMethod(paymentMethod: RecommendationPaymentMethod?) = apply { this.paymentMethod = paymentMethod }

        @Throws(IllegalArgumentException::class)
        fun build(): RecommendationRequest {
            val myPaymentMethod = requireNotNull(paymentMethod)
            return RecommendationRequest(myPaymentMethod)
        }
    }
}
