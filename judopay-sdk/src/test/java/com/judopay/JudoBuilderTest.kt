package com.judopay

import com.judopay.model.Amount
import com.judopay.model.CardNetwork
import com.judopay.model.Currency
import com.judopay.model.PaymentMethod
import com.judopay.model.PaymentWidgetType
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing the Judo configuration object builder logic")
internal class JudoBuilderTest {

    lateinit var judoBuilder: Judo.Builder

    @BeforeEach
    fun setUp() {
        judoBuilder = Judo.Builder(PaymentWidgetType.CARD_PAYMENT)
            .setJudoId("1")
            .setApiToken("1")
            .setApiSecret("1")
            .setAmount(Amount("1", Currency.GBP))
            .setReference(mockk())
            .setPaymentMethods(PaymentMethod.values())
            .setUiConfiguration(mockk())
            .setIsSandboxed(true)
            .setSupportedCardNetworks(CardNetwork.values())
            .setPrimaryAccountDetails(mockk())
            .setGooglePayConfiguration(mockk())
            .setAddress(mockk())
    }

    @Test
    @DisplayName("When no required parameters specified, build() should throw a IllegalArgumentException")
    fun testThatBuildThrows() {
        assertThrows<IllegalArgumentException> {
            Judo.Builder(PaymentWidgetType.CARD_PAYMENT).build()
        }
    }

    @Test
    @DisplayName("When judoId is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnIdNull() {

        assertThrows<IllegalArgumentException> {
            judoBuilder.setJudoId(null).build()
        }
    }

    @Test
    @DisplayName("When judoId is empty, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnIdEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setJudoId("").build()
        }
    }

    @Test
    @DisplayName("When apiToken is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnTokenNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiToken(null).build()
        }
    }

    @Test
    @DisplayName("When apiToken is empty, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnTokenEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiToken("").build()
        }
    }

    @Test
    @DisplayName("When apiSecret is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnSecretNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiSecret(null).build()
        }
    }

    @Test
    @DisplayName("When apiSecret is empty, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnSecretEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiSecret("").build()
        }
    }

    @Test
    @DisplayName("When amount is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnAmountNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setAmount(null).build()
        }
    }

    @Test
    @DisplayName("When reference is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnReferenceNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setReference(null).build()
        }
    }

    @Test
    @DisplayName("When paymentMethods size is 1 and paymentMethod is iDEAL and currency is not EUR, build() should throw a IllegalArgumentException true true true")
    fun testThatBuildThrowsOnPaymentMethodsIdealNotSupported() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setPaymentMethods(arrayOf(PaymentMethod.IDEAL)).build()
        }
    }

    @Test
    @DisplayName("When paymentMethods size is greater than 1, then build() should not throw a IllegalArgumentException false true true")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsIdealNotSupported() {
        assertDoesNotThrow {
            judoBuilder.build()
        }
    }

    @Test
    @DisplayName("When paymentMethods currency is EUR, build() should not throw a IllegalArgumentException false true false")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsIdealNotSupportedWithEur() {
        assertDoesNotThrow {
            judoBuilder.setAmount(Amount("1", Currency.EUR)).build()
        }
    }

    @Test
    @DisplayName("When paymentMethods does not include iDEAL, build() should not throw a IllegalArgumentException true false true")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsNotIncludeIdeal() {
        assertDoesNotThrow {
            judoBuilder.setPaymentMethods(arrayOf(PaymentMethod.CARD)).build()
        }
    }

    @Test
    @DisplayName("When paymentMethods size is 1 and includes iDEAL and currency is EUR, build() should not throw a IllegalArgumentException")
    fun testThatBuildDoesNotThsrowsOnPaymentMethodsSizeOneIncludesIdealAndCurrencyEur() {
        assertDoesNotThrow {
            judoBuilder.setPaymentMethods(arrayOf(PaymentMethod.IDEAL))
                .setAmount(Amount("1", Currency.EUR)).build()
        }
    }

    @Test
    @DisplayName("When paymentMethods is empty, build() with default payment methods")
    fun testThatObjectHasDefaultPaymentMethodsWhenPaymentMethodsEmpty() {
        judoBuilder.setPaymentMethods(emptyArray())
        assertTrue(judoBuilder.build().paymentMethods.contentEquals(arrayOf(PaymentMethod.CARD)))
    }

    @Test
    @DisplayName("When paymentMethods is null, build() with default payment methods")
    fun testThatObjectHasDefaultPaymentMethodsWhenPaymentMethodsNull() {
        judoBuilder.setPaymentMethods(null)
        assertTrue(judoBuilder.build().paymentMethods.contentEquals(arrayOf(PaymentMethod.CARD)))
    }

    @Test
    @DisplayName("When supportedCardNetworks is empty, build() with default supportedCardNetworks")
    fun testThatObjectHasDefaultSupportedCardNetworksWhenSupportedCardNetworksEmpty() {
        judoBuilder.setSupportedCardNetworks(emptyArray())
        assertTrue(
            judoBuilder.build().supportedCardNetworks.contentEquals(
                arrayOf(
                    CardNetwork.VISA,
                    CardNetwork.MASTERCARD,
                    CardNetwork.AMEX,
                    CardNetwork.MAESTRO
                )
            )
        )
    }

    @Test
    @DisplayName("When supportedCardNetworks is null, build() with default supportedCardNetworks")
    fun testThatObjectHasDefaultSupportedCardNetworksWhenSupportedCardNetworksNull() {
        judoBuilder.setSupportedCardNetworks(null)
        assertTrue(
            judoBuilder.build().supportedCardNetworks.contentEquals(
                arrayOf(
                    CardNetwork.VISA,
                    CardNetwork.MASTERCARD,
                    CardNetwork.AMEX,
                    CardNetwork.MAESTRO
                )
            )
        )
    }

    @Test
    @DisplayName("When uiConfiguration is null, build() with default uiConfiguration")
    fun testThatObjectHasDefaultUiConfigurationWhenUiConfigurationNull() {
        judoBuilder.setUiConfiguration(null)
        assertTrue(
            !judoBuilder.build().uiConfiguration.avsEnabled && judoBuilder.build().uiConfiguration.shouldDisplayAmount
        )
    }

    @Test
    @DisplayName("When isSandbox is null, build() with default isSandbox")
    fun testThatObjectHasDefaultIsSandboxWhenIsSandboxNull() {
        judoBuilder.setIsSandboxed(null)
        assertFalse(judoBuilder.build().isSandboxed)
    }
}
