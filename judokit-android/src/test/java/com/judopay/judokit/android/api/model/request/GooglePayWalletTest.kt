package com.judopay.judokit.android.api.model.request

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing Google pay wallet builder")
internal class GooglePayWalletTest {
    private val wallet =
        GooglePayWallet.Builder()
            .setCardDetails("cardDetails")
            .setCardNetwork("network")
            .setToken("token")

    @Test
    @DisplayName("Should throw exception on providing null card network")
    fun exceptionOnNullCardNetwork() {
        assertThrows<IllegalArgumentException> { wallet.setCardNetwork(null).build() }
    }

    @Test
    @DisplayName("Should throw exception on providing empty card network")
    fun exceptionOnEmptyCardNetwork() {
        assertThrows<IllegalArgumentException> { wallet.setCardNetwork("").build() }
    }

    @Test
    @DisplayName("Should throw exception on providing null card details")
    fun exceptionOnNullCardDetails() {
        assertThrows<IllegalArgumentException> { wallet.setCardDetails(null).build() }
    }

    @Test
    @DisplayName("Should throw exception on providing empty card details")
    fun exceptionOnEmptyCardDetails() {
        assertThrows<IllegalArgumentException> { wallet.setCardDetails("").build() }
    }

    @Test
    @DisplayName("Should throw exception on providing null token")
    fun exceptionOnNullToken() {
        assertThrows<IllegalArgumentException> { wallet.setToken(null).build() }
    }

    @Test
    @DisplayName("Should throw exception on providing empty token")
    fun exceptionOnEmptyToken() {
        assertThrows<IllegalArgumentException> { wallet.setToken("").build() }
    }

    @Test
    @DisplayName("Given build is called, when all required fields are valid, then exception not thrown")
    fun exceptionNotThrownOnBuildWithAllFieldsValid() {
        assertDoesNotThrow { wallet.build() }
    }
}
