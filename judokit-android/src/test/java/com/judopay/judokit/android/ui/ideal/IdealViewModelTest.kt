package com.judopay.judokit.android.ui.ideal

import android.app.Application
import androidx.lifecycle.Observer
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.request.IdealSaleRequest
import com.judopay.judokit.android.api.model.response.BankSaleStatusResponse
import com.judopay.judokit.android.api.model.response.IdealSaleResponse
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.OrderStatus
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.service.polling.PollingResult
import com.judopay.judokit.android.service.polling.PollingService
import com.judopay.judokit.android.toMap
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
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
import retrofit2.await

private const val BIC = "bic"
private const val ORDER_ID = "orderId"

@ExperimentalCoroutinesApi
@ExtendWith(com.judopay.judokit.android.InstantExecutorExtension::class)
class IdealViewModelTest {
    private val testDispatcher = TestCoroutineDispatcher()

    private val judo = getJudo()
    private val saleRequest = buildSaleRequest()
    private val service: JudoApiService = mockk(relaxed = true)
    private val pollingService: PollingService = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val sut = IdealViewModel(judo, service, pollingService, application)
    private val saleResponse = mockk<IdealSaleResponse>(relaxed = true)
    private val saleCallResult = JudoApiCallResult.Success(saleResponse)
    private val statusResponse = mockk<BankSaleStatusResponse>(relaxed = true)
    private var statusCallResult = JudoApiCallResult.Success(statusResponse)

    private val isLoadingMock = spyk<Observer<Boolean>>()
    private val isDelayMock = spyk<Observer<Boolean>>()
    private val saleCallResultMock = spyk<Observer<JudoApiCallResult<IdealSaleResponse>>>()
    private val pollingResultMock =
        spyk<Observer<PollingResult<BankSaleStatusResponse>>>()

    @BeforeEach
    internal fun setUp() {
        mockkStatic("retrofit2.KotlinExtensions")

        sut.isLoading.observeForever(isLoadingMock)
        sut.isRequestDelayed.observeForever(isDelayMock)
        sut.saleCallResult.observeForever(saleCallResultMock)
        sut.saleStatusResult.observeForever(pollingResultMock)

        every { saleResponse.orderId } returns ORDER_ID
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.SUCCEEDED

        coEvery {
            service.sale(any<IdealSaleRequest>()).await().hint(JudoApiCallResult::class)
        } returns saleCallResult
        coEvery {
            service.status(any()).await().hint(JudoApiCallResult::class)
        } returns statusCallResult

        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    @DisplayName("Given send method is called with action Initialise, then the loading observable should be set to isLoading = true")
    fun loadingTrueOnInitialise() {
        val slots = mutableListOf<Boolean>()

        sut.send(IdealAction.Initialise(BIC))

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[0]
        assertTrue(isLoading)
    }

    @Test
    @DisplayName("Given send method is called with action Initialise, then sale method should be called")
    fun makeSaleRequestOnPayWithSelectedBank() {
        sut.send(IdealAction.Initialise(BIC))

        coVerify { service.sale(saleRequest) }
    }

    @Test
    @DisplayName("Given send method is called with action Initialise, when sale request is successful and data not null, then update saleCallResult observer")
    fun postSaleResponseAfterSaleRequestFinished() {
        val slots = mutableListOf<JudoApiCallResult<IdealSaleResponse>>()

        sut.send(IdealAction.Initialise(BIC))

        verify { saleCallResultMock.onChanged(capture(slots)) }

        val saleCallResult = slots[0]
        assertEquals(saleCallResult, this.saleCallResult)
    }

    @Test
    @DisplayName("Given send method is called with action Initialise, when sale request is successful and data is null, then update saleCallResult observer with JudoApiCallResult failure")
    fun updateSaleCallResultWithFailure() {
        val emptySaleCallResult = JudoApiCallResult.Success(null)
        val slots = mutableListOf<JudoApiCallResult<IdealSaleResponse>>()
        coEvery {
            service.sale(any<IdealSaleRequest>()).await().hint(JudoApiCallResult::class)
        } returns emptySaleCallResult

        sut.send(IdealAction.Initialise(BIC))

        verify { saleCallResultMock.onChanged(capture(slots)) }

        val saleCallResult = slots[0]

        assertEquals(JudoApiCallResult.Failure(), saleCallResult)
    }

    @Test
    @DisplayName("Given send method is called with action Initialise, when sale request is failed, then update saleCallResult observer with JudoApiCallResult failure")
    fun updateSaleCallResultWithFailureWHenRequestFailed() {
        val failedSaleCallResult = JudoApiCallResult.Failure()
        val slots = mutableListOf<JudoApiCallResult<IdealSaleResponse>>()
        coEvery {
            service.sale(any<IdealSaleRequest>()).await().hint(JudoApiCallResult::class)
        } returns failedSaleCallResult

        sut.send(IdealAction.Initialise(BIC))

        verify { saleCallResultMock.onChanged(capture(slots)) }

        val saleCallResult = slots[0]

        assertEquals(saleCallResult, JudoApiCallResult.Failure())
    }

    @Test
    @DisplayName("Given send is called with action Initialise, when sale request is finished, then set isLoading = false")
    fun loadingFalseOnSaleRequestFinished() {
        val slots = mutableListOf<Boolean>()

        sut.send(IdealAction.Initialise(BIC))

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[1]
        assertFalse(isLoading)
    }

    @Test
    @DisplayName("Given send is called with action StartPolling, then start method of PollingService should be called")
    fun callStartOnStartPollingAction() {
        sut.send(IdealAction.StartPolling)

        coVerify { pollingService.start() }
    }

    @Test
    @DisplayName("Given send is called with action CancelPolling, then cancel method of PollingService should be called")
    fun callCancelOnCancelPollingAction() {
        sut.send(IdealAction.CancelPolling)

        verify { pollingService.cancel() }
    }

    @Test
    @DisplayName("Given send is called with action ResetPolling, then reset method of PollingService should be called")
    fun callResetOnResetPollingAction() {
        sut.send(IdealAction.ResetPolling)

        verify { pollingService.reset() }
    }

    @Test
    @DisplayName("Given send is called with action RetryPolling, then retry method of PollingService should be called")
    fun callRetryOnRetryPollingAction() {
        sut.send(IdealAction.RetryPolling)

        coVerify { pollingService.retry() }
    }

    private fun getJudo() = Judo.Builder(PaymentWidgetType.CARD_PAYMENT)
        .setJudoId("111111111")
        .setAuthorization(mockk(relaxed = true))
        .setAmount(Amount("1", Currency.EUR))
        .setReference(Reference("consumer", "payment"))
        .build()

    private fun buildSaleRequest() = IdealSaleRequest.Builder()
        .setAmount(judo.amount.amount)
        .setMerchantConsumerReference(judo.reference.consumerReference)
        .setMerchantPaymentReference(judo.reference.paymentReference)
        .setPaymentMetadata(judo.reference.metaData?.toMap())
        .setJudoId(judo.judoId)
        .setBic(BIC)
        .build()
}
