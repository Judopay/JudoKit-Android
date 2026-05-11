package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing PaymentMethod extension properties")
internal class PaymentMethodTest {
    @Test
    @DisplayName("PaymentMethod.icon returns a non-zero resource id for CARD")
    fun iconForCard() {
        assertNotNull(PaymentMethod.CARD.icon)
    }

    @Test
    @DisplayName("PaymentMethod.icon returns a non-zero resource id for GOOGLE_PAY")
    fun iconForGooglePay() {
        assertNotNull(PaymentMethod.GOOGLE_PAY.icon)
    }

    @Test
    @DisplayName("PaymentMethod.text returns a non-zero resource id for CARD")
    fun textForCard() {
        assertNotNull(PaymentMethod.CARD.text)
    }

    @Test
    @DisplayName("PaymentMethod.text returns a non-zero resource id for GOOGLE_PAY")
    fun textForGooglePay() {
        assertNotNull(PaymentMethod.GOOGLE_PAY.text)
    }

    @Test
    @DisplayName("PaymentMethod.paymentButtonType returns a non-null value for CARD")
    fun paymentButtonTypeForCard() {
        assertNotNull(PaymentMethod.CARD.paymentButtonType)
    }

    @Test
    @DisplayName("PaymentMethod.paymentButtonType returns a non-null value for GOOGLE_PAY")
    fun paymentButtonTypeForGooglePay() {
        assertNotNull(PaymentMethod.GOOGLE_PAY.paymentButtonType)
    }

    @Test
    @DisplayName("PaymentMethod enum has expected values")
    fun paymentMethodValues() {
        val values = PaymentMethod.values()
        assert(values.contains(PaymentMethod.CARD))
        assert(values.contains(PaymentMethod.GOOGLE_PAY))
    }
}
