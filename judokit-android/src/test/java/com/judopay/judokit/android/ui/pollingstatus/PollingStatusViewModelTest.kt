package com.judopay.judokit.android.ui.pollingstatus

import android.app.Application
import androidx.lifecycle.Observer
import com.judopay.judokit.android.InstantExecutorExtension
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.response.BankSaleStatusResponse
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.service.polling.PollingResult
import com.judopay.judokit.android.service.polling.PollingService
import io.mockk.coEvery
import io.mockk.coVerify
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
import retrofit2.await

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
@DisplayName("Testing PollingStatusViewModel logic")
internal class PollingStatusViewModelTest {
    private val testDispatcher = TestCoroutineDispatcher()

    private val service: JudoApiService = mockk(relaxed = true)
    private val pollingService: PollingService = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)

    private val sut =
        PollingStatusViewModel(
            pollingService,
            application,
        )

    private val saleStatusResult = spyk<Observer<PollingResult<BankSaleStatusResponse>>>()

    @BeforeEach
    internal fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic("retrofit2.KotlinExtensions")

        coEvery {
            service.tokenPayment(any()).await().hint(JudoApiCallResult::class)
        } returns mockk(relaxed = true)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
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
}
