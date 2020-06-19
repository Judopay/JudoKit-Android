package com.judokit.android.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing UiConfiguration builder")
internal class UiConfigurationTest {

    val sut = UiConfiguration.Builder()
        .setAvsEnabled(true)
        .setShouldPaymentMethodsDisplayAmount(true)
        .setShouldPaymentButtonDisplayAmount(true)
        .setShouldPaymentMethodsVerifySecurityCode(true)

    @DisplayName("Given shouldPaymentMethodsDisplayAmount is null, then default value should be set")
    @Test
    fun setDefaultValueOnShouldPaymentMethodsDisplayAmountNull() {
        assertTrue(
            sut.setShouldPaymentMethodsDisplayAmount(null).build().shouldPaymentMethodsDisplayAmount
        )
    }

    @DisplayName("Given shouldPaymentButtonDisplayAmount is null, then default value should be set")
    @Test
    fun setDefaultValueOnShouldPaymentButtonDisplayAmountNull() {
        assertFalse(
            sut.setShouldPaymentButtonDisplayAmount(null).build().shouldPaymentButtonDisplayAmount
        )
    }

    @DisplayName("Given avsEnabled is null, then default value should be set")
    @Test
    fun setDefaultValueOnAvsEnabledNull() {
        assertFalse(sut.setAvsEnabled(null).build().avsEnabled)
    }

    @DisplayName("Given shouldPaymentMethodsVerifySecurityCode is null, then default value should be set")
    @Test
    fun setDefaultValueOnShouldPaymentMethodsVerifySecurityCodeNull() {
        assertTrue(
            sut.setShouldPaymentMethodsVerifySecurityCode(null)
                .build().shouldPaymentMethodsVerifySecurityCode
        )
    }
}
