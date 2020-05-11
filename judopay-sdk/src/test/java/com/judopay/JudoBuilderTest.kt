package com.judopay

import com.judopay.model.Amount
import com.judopay.model.CardNetwork
import com.judopay.model.Currency
import com.judopay.model.PaymentMethod
import com.judopay.model.PaymentWidgetType
import com.judopay.model.Reference
import com.judopay.model.UiConfiguration
import org.junit.jupiter.api.Assertions
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
            .setReference(Reference("1", "1"))
            .setPaymentMethods(PaymentMethod.values())
            .setUiConfiguration(UiConfiguration(avsEnabled = false, shouldDisplayAmount = true))
            .setIsSandboxed(true)
            .setSupportedCardNetworks(CardNetwork.values())
    }

    @Test
    @DisplayName("When no required parameters specified, build() should throw a IllegalArgumentException")
    fun testThatBuildThrows() {
        assertThrows<IllegalArgumentException> {
            Judo.Builder(PaymentWidgetType.CARD_PAYMENT).build()
        }
    }

    @Test
    @DisplayName("When id is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnIdNull() {

        assertThrows<IllegalArgumentException> {
            judoBuilder.setJudoId(null).build()
        }
    }

    @Test
    @DisplayName("When id is empty, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnIdEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setJudoId("").build()
        }
    }

    @Test
    @DisplayName("When token is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnTokenNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiToken(null).build()
        }
    }

    @Test
    @DisplayName("When token is empty, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnTokenEmpty() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiToken("").build()
        }
    }

    @Test
    @DisplayName("When secret is null, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnSecretNull() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setApiSecret(null).build()
        }
    }

    @Test
    @DisplayName("When secret is empty, build() should throw a IllegalArgumentException")
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
    @DisplayName("When paymentMethods size is 1 and paymentMethod is iDEAL and currency is not EUR, build() should throw a IllegalArgumentException")
    fun testThatBuildThrowsOnPaymentMethodsIdealNotSupported() {
        assertThrows<IllegalArgumentException> {
            judoBuilder.setPaymentMethods(arrayOf(PaymentMethod.IDEAL)).build()
        }
    }

    @Test
    @DisplayName("When paymentMethods size is greater than 1 and paymentMethods include iDEAL and currency is not EUR, build() should not throw a IllegalArgumentException")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsIdealNotSupported() {
        Assertions.assertDoesNotThrow {
            judoBuilder.build()
        }
    }

    @Test
    @DisplayName("When paymentMethods size is greater than 1 and paymentMethods include iDEAL and currency is EUR, build() should not throw a IllegalArgumentException")
    fun testThatBuildDoesNotThrowsOnPaymentMethodsIdealNotSupportedWithEur() {
        Assertions.assertDoesNotThrow {
            judoBuilder.setAmount(Amount("1", Currency.EUR)).build()
        }
    }
}
