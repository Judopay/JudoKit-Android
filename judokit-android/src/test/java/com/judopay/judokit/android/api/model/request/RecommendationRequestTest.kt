package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.model.PaymentMethodCipher
import com.judopay.judokit.android.model.RecommendationPaymentMethod
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing Recommendation request builder")
internal class RecommendationRequestTest {

    private val sut = RecommendationRequest.Builder()

    @Test
    @DisplayName("Given a valid payment method, then build() should return RecommendationRequest object")
    fun buildRecommendationRequest() {
        val paymentMethodCipher = PaymentMethodCipher.Builder()
            .setAesKeyCipherText("aesKeyCipherText")
            .setAlgorithm("algorithm")
            .setCardCipherText("cardCipherText")
            .setKeyIndex("keyIndex")
            .setKeySignature("keySignature")
            .setMethodType("methodType")
            .setRecommendationFeatureProviderSDKVersion("recommendationFeatureProviderSDKVersion")
            .build()

        val recommendationPaymentMethod = RecommendationPaymentMethod(paymentMethodCipher)

        val receivedRequestObject = sut.setPaymentMethod(recommendationPaymentMethod).build()

        assertNotNull(receivedRequestObject)
        assertEquals(
            "aesKeyCipherText",
            receivedRequestObject.paymentMethod?.paymentMethodCipher?.aesKeyCipherText
        )
        assertEquals(
            "algorithm",
            receivedRequestObject.paymentMethod?.paymentMethodCipher?.algorithm
        )
        assertEquals(
            "cardCipherText",
            receivedRequestObject.paymentMethod?.paymentMethodCipher?.cardCipherText
        )
        assertEquals(
            "keyIndex",
            receivedRequestObject.paymentMethod?.paymentMethodCipher?.keyIndex
        )
        assertEquals(
            "keySignature",
            receivedRequestObject.paymentMethod?.paymentMethodCipher?.keySignature
        )
        assertEquals(
            "methodType",
            receivedRequestObject.paymentMethod?.paymentMethodCipher?.methodType
        )
        assertEquals(
            "recommendationFeatureProviderSDKVersion",
            receivedRequestObject.paymentMethod?.paymentMethodCipher?.recommendationFeatureProviderSDKVersion
        )
    }

    @Test
    @DisplayName("Given null payment method, then build() should throw IllegalArgumentException")
    fun exceptionOnNullPaymentMethod() {
        assertThrows<IllegalArgumentException> {
            sut.setPaymentMethod(null).build()
        }
    }
}
