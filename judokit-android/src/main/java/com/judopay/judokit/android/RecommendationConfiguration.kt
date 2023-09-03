package com.judopay.judokit.android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Recommendation configuration object that is required for Recommendation Feature.
 */
@Parcelize
class RecommendationConfiguration internal constructor(
    val rsaKey: String,
    val recommendationUrl: String,
    val recommendationTimeout: Int?
) : Parcelable {

    /**
     * Builder class to create a [RecommendationConfiguration] object since it's constructor is private
     */
    class Builder() {
        private var rsaKey: String? = null
        private var recommendationUrl: String? = null
        private var recommendationTimeout: Int? = null

        fun setRsaKey(rsaKey: String?) =
            apply { this.rsaKey = rsaKey }

        fun setRecommendationUrl(recommendationUrl: String?) =
            apply { this.recommendationUrl = recommendationUrl }

        fun setRecommendationTimeout(recommendationTimeout: Int?) =
            apply { this.recommendationTimeout = recommendationTimeout }

        /**
         * Method that initializes Recommendation configuration object that can be used for
         * card verification.
         * @return A new RecommendationConfiguration object that can be added to Judo config object
         * in order to start the recommendation flow.
         * @throws IllegalArgumentException If any of the required fields are empty/null or invalid
         */
        @Throws(IllegalArgumentException::class)
        fun build(): RecommendationConfiguration {
            val myRsaKey = requireNotNull(rsaKey, "rsa_key")
            val myRecommendationUrl = requireNotNull(recommendationUrl, "recommendation_url")
            return RecommendationConfiguration(
                rsaKey = myRsaKey,
                recommendationUrl = myRecommendationUrl,
                recommendationTimeout = recommendationTimeout
            )
        }
    }

    override fun toString(): String {
        return "RecommendationConfiguration(rsaKey='$rsaKey', recommendationUrl=$recommendationUrl, recommendationTimeout=$recommendationTimeout)"
    }
}
