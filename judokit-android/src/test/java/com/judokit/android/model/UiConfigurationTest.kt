package com.judokit.android.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing UiConfiguration builder")
internal class UiConfigurationTest {

    val sut = UiConfiguration.Builder()
        .setAvsEnabled(true)
        .setShouldPaymentMethodsDisplayAmount(true)
        .setShouldPaymentButtonDisplayAmount(true)

    @DisplayName("Given shouldDisplayAmount is null, then build should throw exception")
    @Test
    fun throwExceptionOnShouldDisplayAmountNull() {
        assertThrows<IllegalArgumentException> { sut.setShouldPaymentMethodsDisplayAmount(null).build() }
    }

    @DisplayName("Given shouldPaymentButtonDisplayAmount is null, then build should throw exception")
    @Test
    fun throwExceptionOnShouldPaymentButtonDisplayAmountNull() {
        assertThrows<IllegalArgumentException> {
            sut.setShouldPaymentButtonDisplayAmount(null).build()
        }
    }

    @DisplayName("Given avsEnabled is null, then build should throw exception")
    @Test
    fun throwExceptionOnAvsEnabledNull() {
        assertThrows<IllegalArgumentException> { sut.setAvsEnabled(null).build() }
    }
}
