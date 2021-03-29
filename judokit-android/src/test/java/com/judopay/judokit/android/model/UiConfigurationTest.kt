package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing UiConfiguration builder")
internal class UiConfigurationTest {

    val sut = UiConfiguration.Builder()

    @DisplayName("Given shouldPaymentMethodsDisplayAmount is null, then build should throw exception")
    @Test
    fun throwsOnShouldPaymentMethodsDisplayAmountNull() {
        assertThrows<IllegalArgumentException> {
            sut.setShouldPaymentMethodsDisplayAmount(null).build()
        }
    }

    @DisplayName("Given shouldPaymentMethodsDisplayAmount is not set, then default value should be set")
    @Test
    fun setDefaultValueOnShouldPaymentMethodsDisplayAmountNotSet() {
        assertTrue(sut.build().shouldPaymentMethodsDisplayAmount)
    }

    @DisplayName("Given shouldPaymentButtonDisplayAmount is null, then build should throw exception")
    @Test
    fun throwsOnShouldPaymentButtonDisplayAmountNull() {
        assertThrows<IllegalArgumentException> {
            sut.setShouldPaymentButtonDisplayAmount(null).build()
        }
    }

    @DisplayName("Given shouldPaymentButtonDisplayAmount is not set, then default value should be set")
    @Test
    fun setDefaultValueOnShouldPaymentButtonDisplayAmountNotSet() {
        assertFalse(sut.build().shouldPaymentButtonDisplayAmount)
    }

    @DisplayName("Given avsEnabled is null, then build should throw exception")
    @Test
    fun throwsOnAvsEnabledNull() {
        assertThrows<IllegalArgumentException> {
            (sut.setAvsEnabled(null).build().avsEnabled)
        }
    }

    @DisplayName("Given avsEnabled is not set, then default value should be set")
    @Test
    fun setDefaultValueOnAvsEnabledNotSet() {
        assertFalse(sut.build().avsEnabled)
    }

    @DisplayName("Given shouldPaymentMethodsVerifySecurityCode is null, then build should throw exception")
    @Test
    fun throwsOnShouldPaymentMethodsVerifySecurityCodeNull() {
        assertThrows<IllegalArgumentException> {
            sut.setShouldPaymentMethodsVerifySecurityCode(null)
                .build().shouldPaymentMethodsVerifySecurityCode
        }
    }

    @DisplayName("Given shouldPaymentMethodsVerifySecurityCode is not set, then default value should be set")
    @Test
    fun setDefaultValueOnShouldPaymentMethodsVerifySecurityCodeNotSet() {
        assertTrue(sut.build().shouldPaymentMethodsVerifySecurityCode)
    }
}
