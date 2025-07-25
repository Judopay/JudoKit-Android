package com.judopay.judokit.android.api.model.request

import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing token request builder")
internal class TokenRequestTest {
    private val request =
        TokenRequest
            .Builder()
            .setEndDate("1220")
            .setCv2("452")
            .setEmailAddress("mail@mail.com")
            .setMobileNumber("1234567")
            .setPrimaryAccountDetails(mockk(relaxed = true))
            .setInitialRecurringPayment(false)

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
    @DisplayName("Should throw an exception on providing null amount")
    fun exceptionOnNullAmount() {
        assertThrows<IllegalArgumentException> { request.setAmount(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty amount")
    fun exceptionOnEmptyAmount() {
        assertThrows<IllegalArgumentException> { request.setAmount("").build() }
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
    @DisplayName("Should throw an exception on providing null card token")
    fun exceptionOnNullCardToken() {
        assertThrows<IllegalArgumentException> { request.setCardToken(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty card token")
    fun exceptionOnEmptyCardToken() {
        assertThrows<IllegalArgumentException> { request.setCardToken("").build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing null address")
    fun exceptionOnNullAddress() {
        assertThrows<IllegalArgumentException> { request.setAddress(null).build() }
    }
}
