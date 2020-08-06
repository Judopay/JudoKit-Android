package com.judokit.android.api.model.request

import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing Google pay request builder")
internal class GooglePayRequestTest {

    private val request = GooglePayRequest.Builder()
        .setJudoId("id")
        .setAmount("1")
        .setCurrency("GBP")
        .setYourConsumerReference("consumerRef")
        .setYourPaymentReference("paymentRef")
        .setGooglePayWallet(
            mockk(relaxed = true) {
                every { token } returns "token"
                every { cardNetwork } returns "Visa"
            }
        )
        .setYourPaymentMetaData(mockk(relaxed = true))
        .setPrimaryAccountDetails(mockk(relaxed = true))

    @Test
    @DisplayName("Should throw an exception on providing null judo id")
    fun exceptionOnNullJudoId() {
        assertThrows<IllegalArgumentException> { request.setJudoId(null).build() }
    }

    @Test
    @DisplayName("Should throw an exception on providing empty judo id")
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
        assertThrows<IllegalArgumentException> { request.setCurrency("").build() }
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
    @DisplayName("Should throw an exception on providing null google pay wallet")
    fun exceptionOnNullGooglePayWallet() {
        assertThrows<IllegalArgumentException> { request.setGooglePayWallet(null).build() }
    }

    @Test
    @DisplayName("Should not throw exception when every required field is valid")
    fun exceptionNotThrownOnEveryFieldValid() {
        assertDoesNotThrow { request.build() }
    }

    @Test
    @DisplayName("Given toJudoResult is called, then map fields to JudoResult")
    fun mapFieldsToJudoResultOnToJudoResultCall() {
        assertEquals(BigDecimal(1), request.build().toJudoResult().amount)
        assertEquals("GBP", request.build().toJudoResult().currency)
        assertEquals("consumerRef", request.build().toJudoResult().consumer?.yourConsumerReference)
        assertEquals("paymentRef", request.build().toJudoResult().yourPaymentReference)
        assertEquals("token", request.build().toJudoResult().cardDetails?.token)
        assertEquals("Visa", request.build().toJudoResult().cardDetails?.scheme)
    }
}
