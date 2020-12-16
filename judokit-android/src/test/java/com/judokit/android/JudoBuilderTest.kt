package com.judokit.android

import com.judokit.android.api.JudoApiService
import com.judokit.android.api.error.toJudoError
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.api.model.response.toJudoResult
import com.judokit.android.model.Amount
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.Currency
import com.judokit.android.model.JudoError
import com.judokit.android.model.PaymentMethod
import com.judokit.android.model.PaymentWidgetType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val CARD_TOKEN = "card_token"
private const val SECURITY_CODE = "security_code"

@DisplayName("Testing the Judo configuration object builder logic")
internal class JudoBuilderTest {

    private lateinit var judoBuilder: Judo.Builder
    private val service: JudoApiService = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        mockkStatic("retrofit2.KotlinExtensions")
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
            .setPBBAConfiguration(mockk())
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
    @DisplayName("Given fetchTransactionWithReceiptId is called, when the response is successful, onSuccess callback should be invoked with JudoResult")
    fun invokeOnSuccessOnSuccessfulResponseWhenFetchTransactionWithReceiptIdIsCalled() {
        val result: JudoApiCallResult.Success<Receipt> = mockk(relaxed = true) {
            every { data } returns mockk(relaxed = true)
        }
        val callMock: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
        val callbackMock = slot<Callback<JudoApiCallResult<Receipt>>>()
        every { callMock.enqueue(capture(callbackMock)) } answers {
            callbackMock.captured.onResponse(callMock, Response.success(result))
        }
        every { service.fetchTransactionWithReceiptId("receiptId") } returns callMock

        judoBuilder.build().fetchTransactionWithReceiptId(
            service,
            "receiptId",
            {
                assertEquals(it, result.data?.toJudoResult())
            },
            {
                // no-op
            }
        )
    }

    @Test
    @DisplayName("Given fetchTransactionWithReceiptId is called, when the response is failure, onFailure callback should be invoked with JudoError")
    fun invokeOnFailureOnFailedResponseWhenFetchTransactionWithReceiptIdIsCalled() {
        val result: JudoApiCallResult.Failure = mockk(relaxed = true) {
            every { error } returns mockk(relaxed = true)
        }
        val callMock: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
        val callbackMock = slot<Callback<JudoApiCallResult<Receipt>>>()
        every { callMock.enqueue(capture(callbackMock)) } answers {
            callbackMock.captured.onResponse(callMock, Response.success(result))
        }
        every { service.fetchTransactionWithReceiptId("receiptId") } returns callMock

        judoBuilder.build().fetchTransactionWithReceiptId(
            service,
            "receiptId",
            {
                // no-op
            },
            {
                assertEquals(it, result.error?.toJudoError())
            }
        )
    }

    @Test
    @DisplayName("Given fetchTransactionWithReceiptId is called, when the request responds with HTTP error, then throw exception")
    fun throwExceptionOnHttpErrorWhenFetchTransactionWithReceiptIdIsCalled() {

        val callMock: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
        val callbackMock = slot<Callback<JudoApiCallResult<Receipt>>>()
        every { callMock.enqueue(capture(callbackMock)) } answers {
            callbackMock.captured.onFailure(callMock, mockk())
        }
        every { service.fetchTransactionWithReceiptId("receiptId") } returns callMock

        assertThrows<Exception> {
            judoBuilder.build().fetchTransactionWithReceiptId(
                service,
                "receiptId",
                {
                    // no-op
                },
                {
                    // no-op
                }
            )
        }
    }
}
