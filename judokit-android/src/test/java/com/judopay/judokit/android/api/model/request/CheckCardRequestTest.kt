package com.judopay.judokit.android.api.model.request

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing payment request builder")
internal class CheckCardRequestTest {
    private val request =
        CheckCardRequest.Builder()
            .setStartDate("1220")
            .setIssueNumber("issueNumber")

    @Test
    @DisplayName("Should throw exception on providing null judo id")
    fun exceptionOnNullJudoId() {
        assertThrows<IllegalArgumentException> { request.setJudoId(null).build() }
    }

    @Test
    @DisplayName("Should throw exception on providing empty judo id")
    fun exceptionOnEmptyJudoId() {
        assertThrows<IllegalArgumentException> { request.setJudoId("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null currency")
    fun exceptionOnNullCurrency() {
        assertThrows<IllegalArgumentException> { request.setCurrency(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty currency")
    fun exceptionOnEmptyCurrency() {
        assertThrows<IllegalArgumentException> { request.setJudoId("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null consumer reference")
    fun exceptionOnNullConsumerReference() {
        assertThrows<IllegalArgumentException> { request.setYourConsumerReference(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty consumer reference")
    fun exceptionOnEmptyConsumerReference() {
        assertThrows<IllegalArgumentException> { request.setYourConsumerReference("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null card number")
    fun exceptionOnNullCardNumber() {
        assertThrows<IllegalArgumentException> { request.setCardNumber(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty card number")
    fun exceptionOnEmptyCardNumber() {
        assertThrows<IllegalArgumentException> { request.setCardNumber("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null cv2")
    fun exceptionOnNullCv2() {
        assertThrows<IllegalArgumentException> { request.setCv2(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty cv2")
    fun exceptionOnEmptyCv2() {
        assertThrows<IllegalArgumentException> { request.setCv2("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null expiry date")
    fun exceptionOnNullExpiryDate() {
        assertThrows<IllegalArgumentException> { request.setExpiryDate(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty expiry date")
    fun exceptionOnEmptyExpiryDate() {
        assertThrows<IllegalArgumentException> { request.setExpiryDate("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null payment reference")
    fun exceptionOnNullPaymentReference() {
        assertThrows<IllegalArgumentException> { request.setYourPaymentReference(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty payment reference")
    fun exceptionOnEmptyPaymentReference() {
        assertThrows<IllegalArgumentException> { request.setYourPaymentReference("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null address")
    fun exceptionOnNullAddress() {
        assertThrows<IllegalArgumentException> { request.setAddress(null).build() }
    }
}
