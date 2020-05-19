package com.judokit.android

import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.wallet.PaymentData
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.request.GooglePayRequest
import com.judokit.android.api.model.request.toJudoResult
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.api.model.response.toJudoPaymentResult
import com.judokit.android.model.CardScanningResult
import com.judokit.android.model.INTERNAL_ERROR
import com.judokit.android.model.JudoError
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.JudoResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.service.JudoGooglePayService
import com.judokit.android.ui.common.toGooglePayRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

// ApiException(Status.RESULT_CANCELED)
private const val API_EXCEPTION_STATUS_MESSAGE = "16: "

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
@DisplayName("Testing JudoSharedViewModel logic")
internal class JudoSharedViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val judo: Judo = mockk(relaxed = true)
    private val googlePayService: JudoGooglePayService = mockk(relaxed = true)
    private val judoApiService: JudoApiService = mockk(relaxed = true)

    private val sut = JudoSharedViewModel(judo, googlePayService, judoApiService)

    private val paymentResult = spyk<Observer<JudoPaymentResult>>()
    private val paymentMethodsGooglePayResult = spyk<Observer<JudoPaymentResult>>()
    private val scanCardResult = spyk<Observer<CardScanningResult>>()

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("com.judokit.android.ui.common.MappersKt")
        mockkStatic("com.judokit.android.api.model.request.GooglePayRequestKt")
        mockkStatic("com.judokit.android.api.model.response.JudoApiCallResultKt")

        sut.paymentResult.observeForever(paymentResult)
        sut.paymentMethodsGooglePayResult.observeForever(paymentMethodsGooglePayResult)
        sut.scanCardResult.observeForever(scanCardResult)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @DisplayName("Given send is called with LoadGPayPaymentData action, then checkIfGooglePayIsAvailable is called")
    @Test
    fun checkIfGooglePayIsAvailableCalledOnLoadGPayPaymentDataAction() {
        sut.send(JudoSharedAction.LoadGPayPaymentData)

        coVerify { googlePayService.checkIfGooglePayIsAvailable() }
    }

    @DisplayName("Given send is called with LoadGPayPaymentData action, when checkIfGooglePayIsAvailable returns true, then call loadGooglePayPaymentData")
    @Test
    fun callLoadGooglePayPaymentDataOncheckIfGooglePayIsAvailableTrue() {
        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        coVerify { googlePayService.loadGooglePayPaymentData() }
    }

    @DisplayName("Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is GOOGLE_PAY, then update paymentResult with error")
    @Test
    fun updatePaymentResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithGooglePayWidgetType() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify { paymentResult.onChanged(capture(slots)) }
        val actualPaymentResult = slots[0]
        val expectedPaymentResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                "GooglePay is not supported on your device"
            )
        )
        assertEquals(expectedPaymentResult, actualPaymentResult)
    }

    @DisplayName("Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is PRE_AUTH_GOOGLE_PAY, then update paymentResult with error")
    @Test
    fun updatePaymentResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithPreAuthGooglePayWidgetType() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_GOOGLE_PAY

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify { paymentResult.onChanged(capture(slots)) }
        val actualPaymentResult = slots[0]
        val expectedPaymentResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                "GooglePay is not supported on your device"
            )
        )
        assertEquals(expectedPaymentResult, actualPaymentResult)
    }

    @DisplayName("Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is PAYMENT_METHODS, then update paymentMethodsGooglePayResult with error")
    @Test
    fun updatePaymentMethodsGooglePayResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithPaymentMethodsWidgetType() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
        every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify { paymentMethodsGooglePayResult.onChanged(capture(slots)) }
        val actualPaymentMethodsGooglePayResult = slots[0]
        val expectedPaymentMethodsGooglePayResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                "GooglePay is not supported on your device"
            )
        )
        assertEquals(expectedPaymentMethodsGooglePayResult, actualPaymentMethodsGooglePayResult)
    }

    @DisplayName("Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is PRE_AUTH_PAYMENT_METHODS, then update paymentMethodsGooglePayResult with error")
    @Test
    fun updatePaymentMethodsGooglePayResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithPreAuthPaymentMethodsWidgetType() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify { paymentMethodsGooglePayResult.onChanged(capture(slots)) }
        val actualPaymentMethodsGooglePayResult = slots[0]
        val expectedPaymentMethodsGooglePayResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                "GooglePay is not supported on your device"
            )
        )
        assertEquals(expectedPaymentMethodsGooglePayResult, actualPaymentMethodsGooglePayResult)
    }

    @DisplayName("Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is SERVER_TO_SERVER_PAYMENT_METHODS, then update paymentMethodsGooglePayResult with error")
    @Test
    fun updatePaymentMethodsGooglePayResultWithErrorOnCheckIfGooglePayIsAvailableFalseWithServerToServerPaymentMethodsWidgetType() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
        every { judo.paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify { paymentMethodsGooglePayResult.onChanged(capture(slots)) }
        val actualPaymentMethodsGooglePayResult = slots[0]
        val expectedPaymentMethodsGooglePayResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                "GooglePay is not supported on your device"
            )
        )
        assertEquals(expectedPaymentMethodsGooglePayResult, actualPaymentMethodsGooglePayResult)
    }

    @DisplayName("Given checkIfGooglePayIsAvailable returns false, when paymentWidgetType is not isGooglePayWidget or Payment Methods Widget, then does not update any model")
    @Test
    fun modelNotUpdatedOnCheckIfGooglePayIsAvailableReturnFalseAndWidgetTypeIsNotGpayOrPaymentMethods() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns false
        every { judo.paymentWidgetType } returns PaymentWidgetType.CARD_PAYMENT

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify(inverse = true) { paymentResult.onChanged(capture(slots)) }
        verify(inverse = true) { paymentMethodsGooglePayResult.onChanged(capture(slots)) }
    }

    @DisplayName("Given loadGooglePayPaymentData throws IllegalStateException, then update paymentResult with error")
    @Test
    fun updatePaymentResultWithErrorOnLoadGooglePayPaymentDataThrowsIllegalStateException() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true
        coEvery { googlePayService.loadGooglePayPaymentData() } throws IllegalStateException("Error")
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify { paymentResult.onChanged(capture(slots)) }
        val actualPaymentResult = slots[0]
        val expectedPaymentResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                "Error"
            )
        )
        assertEquals(expectedPaymentResult, actualPaymentResult)
    }

    @DisplayName("Given loadGooglePayPaymentData throws ApiException, then update paymentResult with error")
    @Test
    fun updatePaymentResultWithErrorOnLoadGooglePayPaymentDataThrowsApiException() {
        val slots = mutableListOf<JudoPaymentResult>()

        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true
        coEvery { googlePayService.loadGooglePayPaymentData() } throws ApiException(Status.RESULT_CANCELED)
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        sut.send(JudoSharedAction.LoadGPayPaymentData)

        verify { paymentResult.onChanged(capture(slots)) }
        val actualPaymentResult = slots[0]
        val expectedPaymentResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                API_EXCEPTION_STATUS_MESSAGE
            )
        )
        assertEquals(expectedPaymentResult, actualPaymentResult)
    }

    @DisplayName("Given loadGooglePayPaymentData throws other exception than ApiException or IllegalStateException, then throw exception")
    @Test
    fun throwExceptionOnLoadGooglePayPaymentDataThrowsException() {
        coEvery { googlePayService.checkIfGooglePayIsAvailable() } returns true
        coEvery { googlePayService.loadGooglePayPaymentData() } throws RuntimeException()

        try {
            sut.send(JudoSharedAction.LoadGPayPaymentData)
        } catch (e: Exception) {
            assertTrue(e is RuntimeException)
        }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, then call paymentData.ToGooglePayRequest")
    @Test
    fun callToGooglePayRequestOnSendWithLoadGPayPaymentDataSuccessAction() {
        val paymentData: PaymentData = mockk(relaxed = true)

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        verify { paymentData.toGooglePayRequest(judo) }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is SERVER_TO_SERVER_PAYMENT_METHODS, then call googlePayRequest.toJudoResult")
    @Test
    fun callGooglePayRequestToJudoResultOnLoadGpayPaymentDataSuccessWithServerToServerPaymentMethods() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        verify { googlePayRequest.toJudoResult() }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is SERVER_TO_SERVER_PAYMENT_METHODS, then update paymentMethodsGooglePayResult with success")
    @Test
    fun updatePaymentMethodsGooglePayResultOnLoadGpayPaymentDataSuccessWithServerToServerPaymentMethods() {
        val slots = mutableListOf<JudoPaymentResult>()
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        val judoResult: JudoResult = mockk(relaxed = true)
        every { googlePayRequest.toJudoResult() } returns judoResult
        every { judo.paymentWidgetType } returns PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        verify { paymentMethodsGooglePayResult.onChanged(capture(slots)) }

        val actualPaymentMethodsGooglePayResult = slots[0]

        val expectedPaymentMethodsGooglePayResult =
            JudoPaymentResult.Success(googlePayRequest.toJudoResult())
        assertEquals(expectedPaymentMethodsGooglePayResult, actualPaymentMethodsGooglePayResult)
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is PRE_AUTH_GOOGLE_PAY, then call judoApiService.preAuthGooglePayPayment")
    @Test
    fun callPreAuthGooglePayPaymentOnLoadGpayPaymentDataSuccessWithPreAuthGooglePayPaymentMethod() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_GOOGLE_PAY
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.preAuthGooglePayPayment(googlePayRequest) }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is PRE_AUTH_PAYMENT_METHODS, then call judoApiService.preAuthGooglePayPayment")
    @Test
    fun callPreAuthGooglePayPaymentOnLoadGpayPaymentDataSuccessWithPreAuthPaymentMethod() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.preAuthGooglePayPayment(googlePayRequest) }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is GOOGLE_PAY, then call judoApiService.googlePayPayment")
    @Test
    fun callGooglePayPaymentOnLoadGpayPaymentDataSuccessWithGooglePayPaymentMethod() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.googlePayPayment(googlePayRequest) }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is PAYMENT_METHODS, then call judoApiService.googlePayPayment")
    @Test
    fun callGooglePayPaymentOnLoadGpayPaymentDataSuccessWithWidgetTypePaymentMethods() {
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        coVerify { judoApiService.googlePayPayment(googlePayRequest) }
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, when paymentWidgetType is CARD_PAYMENT, then throw IllegalStateException")
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

    @DisplayName("Given send is called with LoadGPayPaymentDataSuccess action, then update paymentResult")
    @Test
    fun updatePaymentResultOnLoadGpayPaymentDataSuccess() {
        val slots = mutableListOf<JudoPaymentResult>()
        val paymentData: PaymentData = mockk(relaxed = true)
        val googlePayRequest: GooglePayRequest = mockk(relaxed = true)
        val judoApiCallResult: JudoApiCallResult<Receipt> = mockk(relaxed = true)
        val expectedJudoPaymentResult: JudoPaymentResult = mockk(relaxed = true)
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY
        every { paymentData.toGooglePayRequest(judo) } returns googlePayRequest
        coEvery { judoApiService.googlePayPayment(googlePayRequest) } returns judoApiCallResult
        every { judoApiCallResult.toJudoPaymentResult() } returns expectedJudoPaymentResult

        sut.send(JudoSharedAction.LoadGPayPaymentDataSuccess(paymentData))

        verify { paymentResult.onChanged(capture(slots)) }
        val actualPaymentResult = slots[0]
        assertEquals(expectedJudoPaymentResult, actualPaymentResult)
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataError action, then update paymentResult with error")
    @Test
    fun updatePaymentResultWithErrorOnLoadGpayPaymentDataErrorAction() {
        val slots = mutableListOf<JudoPaymentResult>()
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        sut.send(JudoSharedAction.LoadGPayPaymentDataError("Message"))

        verify { paymentResult.onChanged(capture(slots)) }
        val actualPaymentResult = slots[0]
        val expectedPaymentResult = JudoPaymentResult.Error(
            JudoError(
                INTERNAL_ERROR,
                "Message"
            )
        )
        assertEquals(expectedPaymentResult, actualPaymentResult)
    }

    @DisplayName("Given send is called with ScanCardResult action, then update scanCardResult")
    @Test
    fun updateScanCardResultOnScanCardResultAction() {
        val slots = mutableListOf<CardScanningResult>()
        val expectedCardScanResult: CardScanningResult = mockk(relaxed = true)

        sut.send(JudoSharedAction.ScanCardResult(expectedCardScanResult))

        verify { scanCardResult.onChanged(capture(slots)) }
        val actualScanCardResult = slots[0]
        assertEquals(expectedCardScanResult, actualScanCardResult)
    }

    @DisplayName("Given send is called with LoadGPayPaymentDataUserCancelled action, then update paymentResult with UserCancelled")
    @Test
    fun updatePaymentResultOnLoadGpayPaymentDataUserCancelledAction() {
        val slots = mutableListOf<JudoPaymentResult>()
        every { judo.paymentWidgetType } returns PaymentWidgetType.GOOGLE_PAY

        sut.send(JudoSharedAction.LoadGPayPaymentDataUserCancelled)

        verify { paymentResult.onChanged(capture(slots)) }

        val actualPaymentResult = slots[0]
        val expectedPaymentResult = JudoPaymentResult.UserCancelled()
        assertEquals(expectedPaymentResult, actualPaymentResult)
    }
}
