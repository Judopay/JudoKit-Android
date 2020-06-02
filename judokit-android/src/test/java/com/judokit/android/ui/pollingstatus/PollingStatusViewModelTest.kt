package com.judokit.android.ui.pollingstatus

import android.app.Application
import androidx.lifecycle.Observer
import com.judokit.android.InstantExecutorExtension
import com.judokit.android.Judo
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.request.BankSaleRequest
import com.judokit.android.api.model.response.BankSaleResponse
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.model.Amount
import com.judokit.android.model.Currency
import com.judokit.android.model.PaymentMethod
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.Reference
import com.judokit.android.service.polling.PollingResult
import com.judokit.android.service.polling.PollingService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.invoke
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
@DisplayName("Testing PollingStatusViewModel logic")
internal class PollingStatusViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private val service: JudoApiService = mockk(relaxed = true)
    private val pollingService: PollingService = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val judo = getJudo()

    private val sut = PollingStatusViewModel(service, pollingService, application, judo)

    private val payByBankResult = spyk<Observer<JudoApiCallResult<BankSaleResponse>>>()
    private val saleStatusResult = spyk<Observer<PollingResult<BankSaleStatusResponse>>>()

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("com.zapp.library.merchant.util.PBBAAppUtils")

        coEvery {
            service.tokenPayment(any()).hint(JudoApiCallResult::class)
        } returns mockk(relaxed = true)
        coEvery { service.sale(any<BankSaleRequest>()) } returns mockk(relaxed = true)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @DisplayName("Given send with PayWithPayByBank action is called, then make service.sale call")
    @Test
    fun updatePaymentMethodModelOnPayWithPayByBank() {
        sut.send(PollingAction.PayWithPayByBank)

        coVerify { service.sale(any<BankSaleRequest>()) }
    }

    @DisplayName("Given send with PayWithPayByBank action is called, then update payByBankResult")
    @Test
    fun updatePayByBankResultOnPayWithPayByBankAction() {
        val slots = mutableListOf<JudoApiCallResult<BankSaleResponse>>()

        sut.payByBankResult.observeForever(payByBankResult)

        sut.send(PollingAction.PayWithPayByBank)

        verify { payByBankResult.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with StartPolling action is called, then start polling")
    @Test
    fun startPollingOnStartPolling() {
        sut.send(PollingAction.StartPolling("orderId"))

        coVerify { pollingService.start() }
    }

    @DisplayName("Given send with StartPolling action is called, when result is invoked, then update saleStatusResult")
    @Test
    fun updateSaleStatusResultOnPollingResultInvoked() {
        val slots = mutableListOf<PollingResult<BankSaleStatusResponse>>()
        every {
            pollingService.result = captureLambda()
        } answers { lambda<(PollingResult<BankSaleStatusResponse>) -> Unit>().invoke(mockk(relaxed = true)) }
        sut.saleStatusResult.observeForever(saleStatusResult)

        sut.send(PollingAction.StartPolling("orderId"))

        verify { saleStatusResult.onChanged(capture(slots)) }
    }

    @DisplayName("Given send with CancelPolling action is called, then cancel polling")
    @Test
    fun cancelPollingOnCancelPolling() {
        sut.send(PollingAction.CancelPolling)

        verify { pollingService.cancel() }
    }

    @DisplayName("Given send with ResetPolling action is called, then reset polling")
    @Test
    fun resetPollingOnResetPolling() {
        sut.send(PollingAction.ResetPolling)

        verify { pollingService.reset() }
    }

    @DisplayName("Given send with RetryPolling action is called, then retry polling")
    @Test
    fun retryPollingOnRetryPolling() {
        sut.send(PollingAction.RetryPolling)

        coVerify { pollingService.retry() }
    }

    private fun getJudo() = mockk<Judo>(relaxed = true) {
        every { paymentWidgetType } returns PaymentWidgetType.PAYMENT_METHODS
        every { paymentMethods } returns PaymentMethod.values()
        every { judoId } returns "1"
        every { siteId } returns "siteId"
        every { apiToken } returns "token"
        every { apiSecret } returns "secret"
        every { amount } returns Amount("1", Currency.GBP)
        every { reference } returns Reference("consumer", "payment")
    }
}
