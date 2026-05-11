package com.judopay.judokit.android.api.model.request

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing PreAuthGooglePayRequest.Builder")
internal class PreAuthGooglePayRequestTest {
    private val wallet =
        GooglePayWallet
            .Builder()
            .setToken("tok_gpay_123")
            .setCardNetwork("VISA")
            .setCardDetails("1111")
            .build()

    private fun validBuilder() =
        PreAuthGooglePayRequest
            .Builder()
            .setJudoId("100200300")
            .setAmount("1.00")
            .setCurrency("GBP")
            .setYourConsumerReference("consumer-ref")
            .setYourPaymentReference("payment-ref")
            .setGooglePayWallet(wallet)

    @Test
    @DisplayName("Given all required fields, then build() succeeds")
    fun buildWithRequiredFields() {
        assertNotNull(validBuilder().build())
    }

    @Test
    @DisplayName("Given optional fields, then build() succeeds")
    fun buildWithOptionalFields() {
        val request =
            validBuilder()
                .setDelayedAuthorisation(true)
                .setAllowIncrement(false)
                .setCardAddress(Address.Builder().build())
                .build()
        assertNotNull(request)
    }

    @Test
    @DisplayName("Given null judoId, then build() throws IllegalArgumentException")
    fun throwsWhenJudoIdIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setJudoId(null).build()
        }
    }

    @Test
    @DisplayName("Given null wallet, then build() throws IllegalArgumentException")
    fun throwsWhenWalletIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setGooglePayWallet(null).build()
        }
    }

    @Test
    @DisplayName("Given null currency, then build() throws IllegalArgumentException")
    fun throwsWhenCurrencyIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setCurrency(null).build()
        }
    }
}
