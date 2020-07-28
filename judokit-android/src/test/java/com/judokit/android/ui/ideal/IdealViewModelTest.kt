package com.judokit.android.ui.ideal

import android.app.Application
import androidx.lifecycle.Observer
import com.judokit.android.Judo
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.request.IdealSaleRequest
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.IdealSaleResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.OrderStatus
import com.judokit.android.model.Amount
import com.judokit.android.model.Currency
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.Reference
import com.judokit.android.toMap
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private const val BIC = "bic"
private const val ORDER_ID = "orderId"

@ExperimentalCoroutinesApi
@ExtendWith(com.judokit.android.InstantExecutorExtension::class)
class IdealViewModelTest {
    private val testDispatcher = TestCoroutineDispatcher()

    private val judo = getJudo()
    private val saleRequest = buildSaleRequest()
    private val service: JudoApiService = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val sut = IdealViewModel(BIC, judo, service, application)
    private val saleResponse = mockk<IdealSaleResponse>(relaxed = true)
    private val saleCallResult = JudoApiCallResult.Success(saleResponse)
    private val statusResponse = mockk<BankSaleStatusResponse>(relaxed = true)
    private var statusCallResult = JudoApiCallResult.Success(statusResponse)

    private val isLoadingMock = spyk<Observer<Boolean>>()
    private val isDelayMock = spyk<Observer<Boolean>>()
    private val saleCallResultMock = spyk<Observer<JudoApiCallResult<IdealSaleResponse>>>()
    private val saleStatusCallResultMock =
        spyk<Observer<JudoApiCallResult<BankSaleStatusResponse>>>()

    @BeforeEach
    internal fun setUp() {
        sut.isLoading.observeForever(isLoadingMock)
        sut.isRequestDelayed.observeForever(isDelayMock)
        sut.saleCallResult.observeForever(saleCallResultMock)
        sut.saleStatusCallResult.observeForever(saleStatusCallResultMock)

        every { saleResponse.orderId } returns ORDER_ID
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.SUCCEEDED

        coEvery {
            service.sale(any<IdealSaleRequest>()).hint(JudoApiCallResult::class)
        } returns saleCallResult
        coEvery { service.status(any()).hint(JudoApiCallResult::class) } returns statusCallResult

        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    @DisplayName("Given payWithSelectedBank is called, then first observable should be set isLoading = true")
    fun loadingTrueOnPayWithSelectedBank() {
        val slots = mutableListOf<Boolean>()

        sut.payWithSelectedBank()

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[0]
        assertTrue(isLoading)
    }

    @Test
    @DisplayName("Given payWithSelectedBank is called, then sale method should be called")
    fun makeSaleRequestOnPayWithSelectedBank() {
        sut.payWithSelectedBank()

        coVerify { service.sale(saleRequest) }
    }

    @Test
    @DisplayName("Given payWithSelectedBank is called, when sale request finished, then post sale response")
    fun postSaleResponseAfterSaleRequestFinished() {
        val slots = mutableListOf<JudoApiCallResult<IdealSaleResponse>>()

        sut.payWithSelectedBank()

        verify { saleCallResultMock.onChanged(capture(slots)) }

        val saleCallResult = slots[0]
        assertEquals(saleCallResult, this.saleCallResult)
    }

    @Test
    @DisplayName("Given payWithSelectedBank is called, when sale request is finished, then set isLoading = false")
    fun loadingTrueOnSaleRequestFinished() {
        val slots = mutableListOf<Boolean>()

        sut.payWithSelectedBank()

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[1]
        assertFalse(isLoading)
    }

    @Test
    @DisplayName("Given order id is set on payWithSelectedBank, when completeIdealPayment is called, then set isLoading = true")
    fun setIsLoadingTrueOnCompleteIdealPaymentCall() {
        val slots = mutableListOf<Boolean>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[0]
        assertTrue(isLoading)
    }

    @Test
    @DisplayName("Given order id is set on payWithSelectedBank, when completeIdealPayment is called, then set isRequestDelayed = false")
    fun setIsDelayFalseOnCompleteIdealPaymentCall() {
        val slots = mutableListOf<Boolean>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()

        verify { isDelayMock.onChanged(capture(slots)) }

        val isDelay = slots[0]
        assertFalse(isDelay)
    }

    @Test
    @DisplayName("Given order id is set on payWithSelectedBank, when completeIdealPayment is called, then make status request")
    fun makeStatusRequestOnCompleteIdealPayment() {
        sut.payWithSelectedBank()
        sut.completeIdealPayment()

        coVerify { service.status(ORDER_ID) }
    }

    @Test
    @DisplayName("Given completeIdealPayment is called, when the request is successful and order status succeeded, then post saleStatusCallRequest with success response")
    fun postSaleStatusCallRequestOnRequestSuccessAndOrderStatusSucceeded() {
        val slots = mutableListOf<JudoApiCallResult<BankSaleStatusResponse>>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()

        verify { saleStatusCallResultMock.onChanged(capture(slots)) }

        val statusCallResult = slots[0]
        assertEquals(this.statusCallResult, statusCallResult)
    }

    @Test
    @DisplayName("Given completeIdealPayment is called, when the request is successful and order status failed, then post saleStatusCallRequest with Success response")
    fun postSaleStatusCallRequestOnRequestSuccessAndOrderStatusFailure() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.FAILED

        val slots = mutableListOf<JudoApiCallResult<BankSaleStatusResponse>>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()

        verify { saleStatusCallResultMock.onChanged(capture(slots)) }

        val statusCallResult = slots[0]
        assertEquals(this.statusCallResult, statusCallResult)
    }

    @Test
    @DisplayName("Given completeIdealPayment is called, when the request fails, then post saleStatusCallRequest with failure response")
    fun postSaleStatusCallRequestOnRequestFailed() {
        coEvery {
            service.status(any()).hint(JudoApiCallResult::class)
        } returns JudoApiCallResult.Failure()

        val slots = mutableListOf<JudoApiCallResult<BankSaleStatusResponse>>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()

        verify { saleStatusCallResultMock.onChanged(capture(slots)) }

        val statusCallResult = slots[0]
        assertEquals(JudoApiCallResult.Failure(-1, null, null), statusCallResult)
    }

    @Test
    @DisplayName("Given completeIdealPayment is called, when the request is completed, then set isLoading = false")
    fun isLoadingFalseOnStatusRequestCompleted() {
        coEvery {
            service.status(any()).hint(JudoApiCallResult::class)
        } returns JudoApiCallResult.Failure()

        val slots = mutableListOf<Boolean>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[1]
        assertEquals(false, isLoading)
    }

    @Test
    @DisplayName("Given completeIdealPayment is called, when the request is successful and order status pending, then post saleStatusCallRequest with success response")
    fun postSaleStatusCallRequestOnRequestSuccessAndOrderStatusPending() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.PENDING

        val slots = mutableListOf<JudoApiCallResult<BankSaleStatusResponse>>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()
        testDispatcher.advanceUntilIdle()

        verify { saleStatusCallResultMock.onChanged(capture(slots)) }

        val statusCallResult = slots[0]
        assertEquals(this.statusCallResult, statusCallResult)
    }

    @Test
    @DisplayName("Given completeIdealPayment is called, when the request is successful and order status pending, then set isDelay = true")
    fun setIsDelayTrueOnSaleStatusCallRequestSuccessAndOrderStatusPending() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.PENDING

        val slots = mutableListOf<Boolean>()

        sut.payWithSelectedBank()
        sut.completeIdealPayment()
        testDispatcher.advanceUntilIdle()

        verify { isDelayMock.onChanged(capture(slots)) }

        val isDelay = slots[1]
        assertTrue(isDelay)
    }

    private fun getJudo() = Judo.Builder(PaymentWidgetType.CARD_PAYMENT)
        .setJudoId("111111111")
        .setAuthorization(mockk(relaxed = true))
        .setAmount(Amount("1", Currency.EUR))
        .setReference(Reference("consumer", "payment"))
        .build()

    private fun buildSaleRequest() = IdealSaleRequest.Builder()
        .setAmount(BigDecimal(judo.amount.amount))
        .setMerchantConsumerReference(judo.reference.consumerReference)
        .setMerchantPaymentReference(judo.reference.paymentReference)
        .setPaymentMetadata(judo.reference.metaData?.toMap())
        .setJudoId(judo.judoId)
        .setBic(BIC)
        .build()
}
