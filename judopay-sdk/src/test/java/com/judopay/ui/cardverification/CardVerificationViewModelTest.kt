package com.judopay.ui.cardverification

import android.app.Application
import androidx.lifecycle.Observer
import com.judopay.InstantExecutorExtension
import com.judopay.api.JudoApiService
import com.judopay.api.model.response.CardVerificationResult
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
@DisplayName("Testing cardVerificationViewModel logic")
internal class CardVerificationViewModelTest {
    private val testDispatcher = TestCoroutineDispatcher()

    private val application: Application = mockk()
    private val service: JudoApiService = mockk(relaxed = true)
    private val cardVerificationResult: CardVerificationResult = mockk(relaxed = true)
    private val receipt: Receipt = mockk(relaxed = true)
    private val judoApiCallResult = JudoApiCallResult.Success(receipt)
    private val receiptId = "receiptId"

    private val isLoadingMock = spyk<Observer<Boolean>>()
    private val judoApiCallResultMock = spyk<Observer<JudoApiCallResult<Receipt>>>()

    private val viewModel = CardVerificationViewModel(service, application)

    @BeforeEach
    internal fun setUp() {
        viewModel.isLoading.observeForever(isLoadingMock)
        viewModel.judoApiCallResult.observeForever(judoApiCallResultMock)

        coEvery {
            service.complete3dSecure(receiptId, cardVerificationResult)
                .hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    internal fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @DisplayName("Given complete3DSecure is called, then set isLoading = true")
    @Test
    fun loadingTrueOnComplete3DSecure() {
        val slots = mutableListOf<Boolean>()

        viewModel.complete3DSecure(receiptId, cardVerificationResult)

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[0]
        assertTrue(isLoading)
    }

    @DisplayName("Given complete3DSecure is called, then complete3DSecure request should be made")
    @Test
    fun complete3DSecureRequestShouldBeMade() {
        viewModel.complete3DSecure(receiptId, cardVerificationResult)

        coVerify { service.complete3dSecure(receiptId, cardVerificationResult) }
    }

    @DisplayName("Given complete3DSecure is called, when complete3DSecure request is complete, then post response to judoApiCallResult")
    @Test
    fun postResponseToJudoApiCallResult() {
        val slots = mutableListOf<JudoApiCallResult<Receipt>>()

        viewModel.complete3DSecure(receiptId, cardVerificationResult)

        verify { judoApiCallResultMock.onChanged(capture(slots)) }

        val judoApiCallResult = slots[0]
        assertEquals(this.judoApiCallResult, judoApiCallResult)
    }

    @DisplayName("Given complete3DSecure is called, when complete3DSecure request is complete, then set isLoading = false")
    @Test
    fun loadingFalseOnRequestComplete() {
        val slots = mutableListOf<Boolean>()

        viewModel.complete3DSecure(receiptId, cardVerificationResult)

        verify { isLoadingMock.onChanged(capture(slots)) }

        val isLoading = slots[1]
        assertFalse(isLoading)
    }
}
