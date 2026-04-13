package com.judopay.judokit.android

import android.app.Application
import android.content.res.Resources
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.wallet.PaymentData
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.request.GooglePayRequest
import com.judopay.judokit.android.api.model.request.PreAuthGooglePayRequest
import com.judopay.judokit.android.api.model.request.toJudoResult
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.service.JudoGooglePayService
import com.judopay.judokit.android.ui.common.toGooglePayRequest
import com.judopay.judokit.android.ui.common.toPreAuthGooglePayRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.await

// ApiException(Status.RESULT_CANCELED)
private const val API_EXCEPTION_STATUS_MESSAGE = "16: "

@ExperimentalCoroutinesApi
@DisplayName("Testing JudoSharedViewModel logic")
internal class JudoSharedViewModelTest {
    private val testScheduler = StandardTestDispatcher().scheduler
    private val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    private val judo: Judo = mockk(relaxed = true)
    private val googlePayService: JudoGooglePayService = mockk(relaxed = true)
    private val judoApiService: JudoApiService = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val resources: Resources = application.resources

    private val sut = JudoSharedViewModel(judo, googlePayService, judoApiService, application)

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("retrofit2.KotlinExtensions")
        mockkStatic("com.judopay.judokit.android.ui.common.MappersKt")
        mockkStatic("com.judopay.judokit.android.api.model.request.GooglePayRequestKt")
        mockkStatic("com.judopay.judokit.android.api.model.response.JudoApiCallResultKt")
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
    }

    @DisplayName("Given send is called with LoadGPayPaymentData action, then checkIfGooglePayIsAvailable is called")
    @Test
    fun checkIfGooglePayIsAvailableCalledOnLoadGPayPaymentDataAction() {
        sut.send(JudoSharedAction.LoadGPayPaymentData)

        coVerify { googlePayService.checkIfGooglePayIsAvailable() }
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentData action, when checkIfGooglePayIsAvailable returns true, then call loadGooglePayPaymentData",
    )
    @Test
    fun callLoadGooglePayPaymentDataOncheckIfGooglePayIsAvailableTrue() {
        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        coVerify { googlePayService.loadGooglePayPaymentData() }
    }

    @DisplayName(
        "Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is GOOGLE_PAY, then update paymentResultEffect with error",
    )
    @Test
    fun updatePaymentResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithGooglePayWidgetType() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            val expectedPaymentResult =
                JudoPaymentResult.Error(
                    JudoError.googlePayNotSupported(resources, "GooglePay is not supported on your device"),
                )
            assertEquals(expectedPaymentResult, results[0])
        }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is PRE_AUTH_GOOGLE_PAY, then update paymentResultEffect with error",
    )
    @Test
    fun updatePaymentResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithPreAuthGooglePayWidgetType() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_GOOGLE_PAY

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            val expectedPaymentResult =
                JudoPaymentResult.Error(
                    JudoError.googlePayNotSupported(resources, "GooglePay is not supported on your device"),
                )
            assertEquals(expectedPaymentResult, results[0])
        }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is PAYMENT_METHODS, then update paymentMethodsResultEffect with error",
    )
    @Test
    fun updatePaymentMethodsResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithPaymentMethodsWidgetType() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentMethodsResultEffect.collect(results::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            val expectedPaymentResult =
                JudoPaymentResult.Error(
                    JudoError.googlePayNotSupported(resources, "GooglePay is not supported on your device"),
                )
            assertEquals(expectedPaymentResult, results[0])
        }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is PRE_AUTH_PAYMENT_METHODS, then update paymentMethodsResultEffect with error",
    )
    @Test
    fun updatePaymentMethodsResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithPreAuthPaymentMethodsWidgetType() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentMethodsResultEffect.collect(results::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            val expectedPaymentResult =
                JudoPaymentResult.Error(
                    JudoError.googlePayNotSupported(resources, "GooglePay is not supported on your device"),
                )
            assertEquals(expectedPaymentResult, results[0])
        }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is SERVER_TO_SERVER_PAYMENT_METHODS, then update paymentMethodsResultEffect with error",
    )
    @Test
    fun updatePaymentMethodsResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithServerToServerPaymentMethodsWidgetType() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentMethodsResultEffect.collect(results::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            val expectedPaymentResult =
                JudoPaymentResult.Error(
                    JudoError.googlePayNotSupported(resources, "GooglePay is not supported on your device"),
                )
            assertEquals(expectedPaymentResult, results[0])
        }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is not isGooglePayWidget or Payment Methods Widget, then does not emit to any effect",
    )
    @Test
    fun noEffectEmittedOnCheckIfGooglePayIsAvailableReturnFalseAndWidgetTypeIsNotGpayOrPaymentMethods() =
        runTest(testScheduler) {
            val paymentResults = mutableListOf<JudoPaymentResult>()
            val methodsResults = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(paymentResults::add)
            }
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentMethodsResultEffect.collect(methodsResults::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
            every { judo.paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            assertTrue(paymentResults.isEmpty())
            assertTrue(methodsResults.isEmpty())
        }

    @DisplayName("Given loadGooglePayPaymentData throws IllegalStateException, then update paymentResultEffect with error")
    @Test
    fun updatePaymentResultWithErrorOnLoadGooglePayPaymentDataThrowsIllegalStateException() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true
            coEvery { googlePayService.loadGooglePayPaymentData() } throws IllegalStateException("Error")
            every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            val expectedPaymentResult =
                JudoPaymentResult.Error(JudoError.googlePayNotSupported(resources, "Error"))
            assertEquals(expectedPaymentResult, results[0])
        }

    @DisplayName("Given loadGooglePayPaymentData throws ApiException, then update paymentResultEffect with error")
    @Test
    fun updatePaymentResultWithErrorOnLoadGooglePayPaymentDataThrowsApiException() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true
            coEvery { googlePayService.loadGooglePayPaymentData() } throws ApiException(Status.RESULT_CANCELED)
            every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

            sut.send(JudoSharedAction.LoadGPayPaymentData)

            val expectedPaymentResult =
                JudoPaymentResult.Error(
                    JudoError.googlePayNotSupported(resources, API_EXCEPTION_STATUS_MESSAGE),
                )
            assertEquals(expectedPaymentResult, results[0])
        }

    @DisplayName(
        "Given loadGooglePayPaymentData throws other exception than ApiException or IllegalStateException, then no effect is emitted",
    )
    @Test
    fun noEffectEmittedWhenLoadGooglePayPaymentDataThrowsUnexpectedException() {
        val paymentResults = mutableListOf<JudoPaymentResult>()
        val methodsResults = mutableListOf<JudoPaymentResult>()

        assertThrows<RuntimeException> {
            runTest(testScheduler) {
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.paymentResultEffect.collect(paymentResults::add)
                }
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    sut.paymentMethodsResultEffect.collect(methodsResults::add)
                }

                coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true
                coEvery { googlePayService.loadGooglePayPaymentData() } throws RuntimeException()
                every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

                sut.send(JudoSharedAction.LoadGPayPaymentData)

                assertTrue(paymentResults.isEmpty())
                assertTrue(methodsResults.isEmpty())
            }
        }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, then call paymentData.ToGooglePayRequest")
    @Test
    fun callToGooglePayRequestOnSendWithLoadGPayPaymentDataSuccessAction() {
        val paymentData: PaymentData = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        verify { paymentData.toGooglePayRequest(judo) }
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is SERVER_TO_SERVER_PAYMENT_METHODS, then call googlePayRequest.toJudoResult",
    )
    @Test
    fun callGooglePayRequestToJudoResultOnLoadGpayPaymentDataSuccessWithServerToServerPaymentMethods() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        verify { googlePayRequest.toJudoResult() }
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is SERVER_TO_SERVER_PAYMENT_METHODS, then emit to paymentMethodsResultEffect with success",
    )
    @Test
    fun updatePaymentMethodsResultOnLoadGpayPaymentDataSuccessWithServerToServerPaymentMethods() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentMethodsResultEffect.collect(results::add)
            }

            val paymentData: PaymentData = mockk(relaxed = true)
            val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
            val judoResult: JudoResult = mockk(relaxed = true)
            every { googlePayRequest.toJudoResult() } returns judoResult
            every { judo.paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
            every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

            sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

            val expectedResult = JudoPaymentResult.Success(googlePayRequest.toJudoResult())
            assertEquals(expectedResult, results[0])
        }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is PRE_AUTH_GOOGLE_PAY, then call judoApiService.preAuthGooglePayPayment",
    )
    @Test
    fun callPreAuthGooglePayPaymentOnLoadGpayPaymentDataSuccessWithPreAuthGooglePayPaymentMethod() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val preAuthGooglePayRequest: PreAuthGooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_GOOGLE_PAY
        every { paymentData.toPreAuthGooglePayRequest(judo) } returns preAuthGooglePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.preAuthGooglePayPayment(preAuthGooglePayRequest) }
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is PRE_AUTH_PAYMENT_METHODS, then call judoApiService.preAuthGooglePayPayment",
    )
    @Test
    fun callPreAuthGooglePayPaymentOnLoadGpayPaymentDataSuccessWithPreAuthPaymentMethod() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val preAuthGooglePayRequest: PreAuthGooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS
        every { paymentData.toPreAuthGooglePayRequest(judo) } returns preAuthGooglePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.preAuthGooglePayPayment(preAuthGooglePayRequest) }
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is GOOGLE_PAY, then call judoApiService.googlePayPayment",
    )
    @Test
    fun callGooglePayPaymentOnLoadGpayPaymentDataSuccessWithGooglePayPaymentMethod() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.googlePayPayment(googlePayRequest) }
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is PAYMENT_METHODS, then call judoApiService.googlePayPayment",
    )
    @Test
    fun callGooglePayPaymentOnLoadGpayPaymentDataSuccessWithWidgetTypePaymentMethods() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.googlePayPayment(googlePayRequest) }
    }

    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is CARD_PAYMENT, then throw IllegalStateException",
    )
    @Test
    fun throwIllegalStateExceptionOnLoadGpayPaymentDataSuccessWithWidgetTypeCardPayment() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        try {
            sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))
        } catch (e: Exception) {
            assertTrue(e is IllegalStateException)
        }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, then update paymentResultEffect")
    @Test
    fun updatePaymentResultOnLoadGpayPaymentDataSuccess() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            val paymentData: PaymentData = mockk(relaxed = true)
            val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
            val judoApiCallResult: JudoApiCallResult<Receipt> = mockk(relaxed = true)
            val expectedJudoPaymentResult: JudoPaymentResult = mockk(relaxed = true)
            every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY
            every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest
            coEvery {
                judoApiService.googlePayPayment(googlePayRequest).await()
            } returns judoApiCallResult
            every { judoApiCallResult.toJudoPaymentResult(resources) } returns expectedJudoPaymentResult

            sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

            assertEquals(expectedJudoPaymentResult, results[0])
        }

    @DisplayName("Given send is called with LoadGPayPaymentDataError action, then update paymentResultEffect with error")
    @Test
    fun updatePaymentResultWithErrorOnLoadGpayPaymentDataErrorAction() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

            sut.send(JudoSharedAction.LoadGPayPaymentDataError("Message"))

            val expectedPaymentResult =
                JudoPaymentResult.Error(JudoError.googlePayNotSupported(resources, "Message"))
            assertEquals(expectedPaymentResult, results[0])
        }

    @DisplayName("Given send is called with LoadGPayPaymentDataUserCancelled action, then update paymentResultEffect with UserCancelled")
    @Test
    fun updatePaymentResultOnLoadGpayPaymentDataUserCancelledAction() =
        runTest(testScheduler) {
            val results = mutableListOf<JudoPaymentResult>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.paymentResultEffect.collect(results::add)
            }

            every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

            sut.send(JudoSharedAction.LoadGPayPaymentDataUserCancelled)

            assertEquals(JudoPaymentResult.UserCancelled(), results[0])
        }
}
