package com.judokit.android

import com.judokit.android.model.Amount
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.Currency
import com.judokit.android.model.PaymentMethod
import com.judokit.android.model.PaymentWidgetType
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

    private lateinit var judoBuilder: Judo.Builder

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
    @DisplayName("Given no required parameters specified, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrows() {
        assertThrows<IllegalArgumentException> {
            Judo.Builder(PaymentWidgetType.CARD_PAYMENT).build()
        }
    }

    @Test
    @DisplayName("Given judoId is null, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnIdNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setJudoId(null).build()
        }
    }

    @Test
    @DisplayName("Given judoId is empty, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnIdEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setJudoId("").build()
        }
    }

    @Test
    @DisplayName("Given apiToken is null, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnTokenNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiToken(null).build()
        }
    }

    @Test
    @DisplayName("Given apiToken is empty, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnTokenEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiToken("").build()
        }
    }

    @Test
    @DisplayName("Given apiSecret is null, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnSecretNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiSecret(null).build()
        }
    }

    @Test
    @DisplayName("Given apiSecret is empty, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnSecretEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiSecret("").build()
        }
    }

    @Test
    @DisplayName("Given amount is null, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnAmountNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setAmount(null).build()
        }
    }

    @Test
    @DisplayName("Given reference is null, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnReferenceNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setReference(null).build()
        }
    }

    @Test
    @DisplayName("Given paymentMethods size is 1 and paymentMethod is iDEAL and currency is not EUR, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnPaymentMethodsIdealNotSupported() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setPaymentMethods(arrayOf(PaymentMethod.IDEAL)).build()
        }
    }

    @Test
    @DisplayName("Given paymentMethods size is greater than 1, then build() should not throw a IllegalArgumentException")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsIdealNotSupported() {
        assertDoesNotThrow {
            judoBuilder.build()
        }
    }

    @Test
    @DisplayName("Given paymentMethods currency is EUR, then build() should not throw a IllegalArgumentException")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsIdealNotSupportedWithEur() {
        assertDoesNotThrow {
            judoBuilder.setAmount(Amount("1", Currency.EUR)).build()
        }
    }

    @Test
    @DisplayName("Given paymentMethods does not include iDEAL, then build() should not throw a IllegalArgumentException")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsNotIncludeIdeal() {
        assertDoesNotThrow {
            judoBuilder.setPaymentMethods(arrayOf(PaymentMethod.CARD)).build()
        }
    }

    @Test
    @DisplayName("Given paymentMethods size is 1 and includes iDEAL and currency is EUR, then build() should not throw a IllegalArgumentException")
    fun testThatBuildDoesNotThsrowsOnPaymentMethodsSizeOneIncludesIdealAndCurrencyEur() {
        assertDoesNotThrow {
            judoBuilder.setPaymentMethods(arrayOf(PaymentMethod.IDEAL))
                .setAmount(Amount("1", Currency.EUR)).build()
        }
    }

    @Test
    @DisplayName("Given paymentMethods is empty, then build() with default payment methods")
    fun testThatObjectHasDefaultPaymentMethodsWhenPaymentMethodsEmpty() {
        judoBuilder.setPaymentMethods(emptyArray())

        assertTrue(judoBuilder.build().paymentMethods.contentEquals(arrayOf(PaymentMethod.CARD)))
    }

    @Test
    @DisplayName("Given paymentMethods is null, then build() with default payment methods")
    fun testThatObjectHasDefaultPaymentMethodsWhenPaymentMethodsNull() {
        judoBuilder.setPaymentMethods(null)

        assertTrue(judoBuilder.build().paymentMethods.contentEquals(arrayOf(PaymentMethod.CARD)))
    }

    @Test
    @DisplayName("Given supportedCardNetworks is empty, then build() with default supportedCardNetworks")
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
    @DisplayName("Given supportedCardNetworks is null, then build() with default supportedCardNetworks")
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
    @DisplayName("Given uiConfiguration is null, then build() with default uiConfiguration")
    fun testThatObjectHasDefaultUiConfigurationWhenUiConfigurationNull() {
        judoBuilder.setUiConfiguration(null)

        assertFalse(judoBuilder.build().uiConfiguration.avsEnabled)
        assertTrue(judoBuilder.build().uiConfiguration.shouldDisplayAmount)
    }

    @Test
    @DisplayName("Given isSandbox is null, then build() with default isSandbox")
    fun testThatObjectHasDefaultIsSandboxWhenIsSandboxNull() {
        judoBuilder.setIsSandboxed(null)

        assertFalse(judoBuilder.build().isSandboxed)
    }
}
