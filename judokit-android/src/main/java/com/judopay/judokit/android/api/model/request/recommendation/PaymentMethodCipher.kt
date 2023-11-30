package com.judopay.judokit.android.api.model.request.recommendation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.judopay.judokit.android.requireNotNullOrEmpty
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethodCipher internal constructor(
    var aesKeyCipherText: String,
    var algorithm: String,
    var cardCipherText: String,
    var keyIndex: String,
    var keySignature: String,
    var methodType: String,
    @SerializedName("ravelinSDKVersion")
    var recommendationFeatureProviderSDKVersion: String
) : Parcelable {
    class Builder {
        private var aesKeyCipherText: String? = null
        private var algorithm: String? = null
        private var cardCipherText: String? = null
        private var keyIndex: String? = null
        private var keySignature: String? = null
        private var methodType: String? = null
        private var recommendationFeatureProviderSDKVersion: String? = null

        fun setAesKeyCipherText(aesKeyCipherText: String?) = apply { this.aesKeyCipherText = aesKeyCipherText }
        fun setAlgorithm(algorithm: String?) = apply { this.algorithm = algorithm }
        fun setCardCipherText(cardCipherText: String?) = apply { this.cardCipherText = cardCipherText }
        fun setKeyIndex(keyIndex: String?) = apply { this.keyIndex = keyIndex }
        fun setKeySignature(keySignature: String?) = apply { this.keySignature = keySignature }
        fun setMethodType(methodType: String?) = apply { this.methodType = methodType }
        fun setRecommendationFeatureProviderSDKVersion(recommendationFeatureProviderSDKVersion: String?) = apply {
            this.recommendationFeatureProviderSDKVersion = recommendationFeatureProviderSDKVersion
        }

        @Throws(IllegalArgumentException::class)
        fun build(): PaymentMethodCipher {
            val myAesKeyCipherText = requireNotNullOrEmpty(aesKeyCipherText, "aesKeyCipherText")
            val myAlgorithm = requireNotNullOrEmpty(algorithm, "algorithm")
            val myCardCipherText = requireNotNullOrEmpty(cardCipherText, "cardCipherText")
            val myKeyIndex = requireNotNull(keyIndex)
            val myKeySignature = requireNotNullOrEmpty(keySignature, "keySignature")
            val myMethodType = requireNotNullOrEmpty(methodType, "methodType")
            val myRecommendationFeatureProviderSDKVersion = requireNotNullOrEmpty(
                recommendationFeatureProviderSDKVersion,
                "recommendationFeatureProviderSDKVersion"
            )

            return PaymentMethodCipher(
                myAesKeyCipherText,
                myAlgorithm,
                myCardCipherText,
                myKeyIndex,
                myKeySignature,
                myMethodType,
                myRecommendationFeatureProviderSDKVersion
            )
        }
    }
}
