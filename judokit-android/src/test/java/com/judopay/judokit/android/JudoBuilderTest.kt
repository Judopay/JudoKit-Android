package com.judopay.judokit.android

import com.google.common.truth.Truth.assertThat
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.NetworkTimeout
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.SubProductInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
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
            .setJudoId("111111111")
            .setAuthorization(mockk(relaxed = true))
            .setAmount(Amount("1", Currency.GBP))
            .setReference(
                mockk(relaxed = true) {
                    every { consumerReference } returns "consumer"
                    every { paymentReference } returns "payment"
                }
            )
            .setPaymentMethods(PaymentMethod.values())
            .setIsSandboxed(true)
            .setSupportedCardNetworks(CardNetwork.values())
            .setPrimaryAccountDetails(mockk())
            .setGooglePayConfiguration(mockk())
            .setAddress(mockk())
            .setPBBAConfiguration(mockk(relaxed = true))
            .setInitialRecurringPayment(false)
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
    @DisplayName("Given judoId does not match regex, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnIdNotMatchingRegex() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setJudoId("1111").build()
        }
    }

    @Test
    @DisplayName("Given judoId matches regex, then build() should not throw exception")
    fun testThatBuildDoesNotThrowOnIdMatchingRegex() {
        assertDoesNotThrow {
            judoBuilder.setJudoId("111-111-111").build()
            judoBuilder.setJudoId("111111111").build()
            judoBuilder.setJudoId("111111").build()
        }
    }

    @Test
    @DisplayName("Given authorization is null, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnAuthorizationNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setAuthorization(null).build()
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
    @DisplayName("Given paymentMethods size is 1 and paymentMethod is PAY_BY_BANK and currency is not GBP, then build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnPaymentMethodsPBBANotSupported() {
        assertThrows<IllegalArgumentException> {
            judoBuilder
                .setAmount(Amount("1", Currency.EUR))
                .setPaymentMethods(arrayOf(PaymentMethod.PAY_BY_BANK)).build()
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
    @DisplayName("Given uiConfiguration is null, then build() should throw exception")
    fun testThatBuildThrowsExceptionWhenUiConfigurationNull() {
        judoBuilder.setUiConfiguration(null)

        assertThrows<IllegalArgumentException> { judoBuilder.build() }
    }

    @Test
    @DisplayName("Given uiConfiguration is not specified, then build() with default uiConfiguration")
    fun testThatObjectHasDefaultUiConfigurationWhenUiConfigurationNotSpecified() {
        assertFalse(judoBuilder.build().uiConfiguration.avsEnabled)
        assertTrue(judoBuilder.build().uiConfiguration.shouldPaymentMethodsDisplayAmount)
        assertTrue(judoBuilder.build().uiConfiguration.shouldPaymentMethodsVerifySecurityCode)
    }

    @Test
    @DisplayName("Given isSandbox is null, then build() with default isSandbox")
    fun testThatObjectHasDefaultIsSandboxWhenIsSandboxNull() {
        judoBuilder.setIsSandboxed(null)

        assertFalse(judoBuilder.build().isSandboxed)
    }

    @Test
    @DisplayName("Given setInitialRecurringPayment is called with value false, then initialRecurringPayment should return false")
    fun initialRecurringPaymentShouldReturnFalse() {
        judoBuilder.setInitialRecurringPayment(false)

        assertFalse(judoBuilder.build().initialRecurringPayment!!)
    }

    @Test
    @DisplayName("Given setNetworkTimeout is called with value null, then apply default timeout values")
    fun applyDefaultTimeoutValuesWhenSetNetworkTimeoutParameterNull() {
        judoBuilder.setNetworkTimeout(null)

        assertEquals(5L, judoBuilder.build().networkTimeout.connectTimeout)
        assertEquals(180L, judoBuilder.build().networkTimeout.readTimeout)
        assertEquals(30L, judoBuilder.build().networkTimeout.writeTimeout)
    }

    @Test
    @DisplayName("Given setNetworkTimeout is called with a new NetworkTimeout parameter, then apply custom timeout values")
    fun applyCustomTimeoutValuesWhenSetNetworkTimeoutParameterIsSet() {
        val connectTimeout = 10L
        val readTimeout = 200L
        val writeTimeout = 80L
        judoBuilder.setNetworkTimeout(
            NetworkTimeout.Builder()
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .setWriteTimeout(writeTimeout)
                .build()
        )

        assertEquals(connectTimeout, judoBuilder.build().networkTimeout.connectTimeout)
        assertEquals(readTimeout, judoBuilder.build().networkTimeout.readTimeout)
        assertEquals(writeTimeout, judoBuilder.build().networkTimeout.writeTimeout)
    }

    @Test
    fun `Given setCardToken is called with a non-null value, then apply that value during build`() {
        val cardToken = CardToken()
        judoBuilder.setCardToken(cardToken)

        val judo = judoBuilder.build()
        assertThat(judo.cardToken).isEqualTo(cardToken)
    }

    @Test
    fun `Given setCardSecurityCode is called with a non-null value, then apply that value during build`() {
        judoBuilder.setCardSecurityCode("security code")
        assertThat(judoBuilder.build().cardSecurityCode).isEqualTo("security code")
    }

    @Test
    fun `Given cardToken is set, toString contains the token data`() {
        val cardToken = CardToken(lastFour = "1234", token = "token", type = 2)
        judoBuilder.setCardToken(cardToken)

        assertThat(judoBuilder.build().toString()).contains(", cardToken=CardToken(cardLastfour=1234, cardToken=token, cardType=2)")
    }

    @Test
    fun `Given subProductInfo is not set, then apply Unknown value during build`() {
        val judo = judoBuilder.build()
        assertThat(judo.subProductInfo).isNotNull()
        assertThat(judo.subProductInfo).isInstanceOf(SubProductInfo.Unknown::class.java)
    }

    @Test
    fun `Given subProductInfo is set to ReactNative, then apply that value during build`() {
        judoBuilder.setSubProductInfo(SubProductInfo.ReactNative("4.0.0"))

        val judo = judoBuilder.build()
        assertThat(judo.subProductInfo).isNotNull()
        assertThat(judo.subProductInfo).isInstanceOf(SubProductInfo.ReactNative::class.java)
        assertThat((judo.subProductInfo as SubProductInfo.ReactNative).version).isEqualTo("4.0.0")
    }
}
