package com.judopay.judokit.android.model

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.judopay.judokit.android.requireNotNullOrEmpty
import com.judopay.judokit.android.ui.common.REGEX_URL
import kotlinx.parcelize.Parcelize

/**
 * Recommendation configuration object that is required for Recommendation Feature.
 */
@Parcelize
@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
class RecommendationConfiguration internal constructor(
    val url: String,
    val rsaPublicKey: String,
    val timeout: Int?,
) : Parcelable {
    /**
     * Builder class to create a [RecommendationConfiguration] object since it's constructor is private
     */
    class Builder {
        private var rsaPublicKey: String? = null
        private var url: String? = null
        private var timeout: Int? = null

        fun setRsaPublicKey(rsaKey: String?) = apply { this.rsaPublicKey = rsaKey }

        fun setUrl(recommendationUrl: String?) = apply { this.url = recommendationUrl }

        fun setTimeout(recommendationTimeout: Int?) = apply { this.timeout = recommendationTimeout }

        /**
         * Method that initializes Recommendation configuration object that can be used for
         * card verification.
         * @return A new RecommendationConfiguration object that can be added to Judo config object
         * in order to start the recommendation flow.
         * @throws IllegalArgumentException If any of the required fields are empty/null or invalid
         */
        @Throws(IllegalArgumentException::class)
        fun build(): RecommendationConfiguration {
            val myRsaKey =
                requireNotNullOrEmpty(
                    rsaPublicKey,
                    "rsaPublicKey",
                    "The RSAPublicKey field in the ravelin recommendation configuration is required.",
                )

            val myRecommendationUrl = requireUrl(url)

            return RecommendationConfiguration(
                rsaPublicKey = myRsaKey,
                url = myRecommendationUrl,
                timeout = timeout,
            )
        }

        @Throws(IllegalArgumentException::class)
        private fun requireUrl(url: String?): String {
            val myUrl = requireNotNullOrEmpty(url, "url", "The URL field in the recommendation configuration is required.")
            require(myUrl.matches(REGEX_URL.toRegex())) {
                "The URL value provided in the recommendation configuration is invalid."
            }
            return myUrl
        }
    }

    override fun toString(): String {
        return "RecommendationConfiguration(url=$url, rsaPublicKey='$rsaPublicKey', timeout=$timeout)"
    }
}
