package com.judopay.judokit.android.api.model.request

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing save card request builder")
internal class SaveCardRequestTest {
    private val request =
        SaveCardRequest
            .Builder()
            .setIssueNumber("123")
            .setStartDate("1220")
            .setEmailAddress("email@mail.com")
            .setMobileNumber("1234567")

    private fun validBuilder() =
        SaveCardRequest
            .Builder()
            .setJudoId("100200300")
            .setCurrency("GBP")
            .setYourConsumerReference("consumer-ref")
            .setYourPaymentReference("payment-ref")
            .setCardNumber("4111111111111111")
            .setCv2("452")
            .setExpiryDate("12/25")

    @Test
    @DisplayName("Given all required fields, then build() succeeds")
    fun buildWithRequiredFields() {
        assertNotNull(validBuilder().build())
    }

    @Test
    @DisplayName("Given optional fields, then build() succeeds")
    fun buildWithOptionalFields() {
        assertNotNull(
            validBuilder()
                .setStartDate("01/20")
                .setIssueNumber("01")
                .setEmailAddress("test@example.com")
                .setMobileNumber("07700900000")
                .setCardHolderName("Jane Doe")
                .setAddress(Address.Builder().build())
                .setDisableNetworkTokenisation(true)
                .setYourPaymentMetaData(mapOf("key" to "value"))
                .build(),
        )
    }

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
}
