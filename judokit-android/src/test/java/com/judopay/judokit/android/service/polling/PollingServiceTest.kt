package com.judopay.judokit.android.service.polling

import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.response.BankSaleStatusResponse
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.OrderStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import retrofit2.await

@DisplayName("Testing polling logic")
internal class PollingServiceTest {
    private val service: JudoApiService = mockk(relaxed = true)
    private lateinit var actualResult: PollingResult<BankSaleStatusResponse>

    private val sut =
        PollingService(service).apply {
            orderId = "orderId"
            result = { actualResult = it }
        }

    private val statusResponse =
        mockk<BankSaleStatusResponse>(relaxed = true) {
            every { orderDetails.orderStatus } returns OrderStatus.SUCCEEDED
        }

    private val statusCallResult: JudoApiCallResult.Success<BankSaleStatusResponse> =
        mockk(relaxed = true) {
            every { data } returns statusResponse
        }

    @Before
    fun setUp() {
        mockkStatic("retrofit2.KotlinExtensions")
        coEvery {
            service.status("orderId").await().hint(JudoApiCallResult::class)
        } returns statusCallResult
    }

    @DisplayName("Given polling starts, when timeout>0, then make status call")
    @Test
    fun makeStatusCallWhenPollingStarts() {
        runTest { sut.start() }

        coVerify { service.status("orderId") }
    }

    @DisplayName("Given status call is successful, when data is not null, invoke result")
    @Test
    fun invokeResultWhenStatusSuccessful() {
        runTest { sut.start() }

        val expectedResult = PollingResult.Success(statusResponse)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given order status is SUCCEEDED, then invoke result with PollingResult SUCCEEDED")
    @Test
    fun invokePollingResultSuccessOnOrderStatusSuccess() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.SUCCEEDED

        runTest { sut.start() }

        val expectedResult = PollingResult.Success(statusResponse)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given order status is FAILED, then invoke result with PollingResult FAILED")
    @Test
    fun invokePollingResultSuccessOnOrderStatusFailed() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.FAILED

        runTest { sut.start() }

        val expectedResult = PollingResult.Failure(statusResponse)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given order status is PENDING, then invoke result with PollingResult Retry")
    @Test
    fun invokePollingResultProcessingOnOrderStatusPending() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.PENDING

        runTest { sut.start() }

        val expectedResult = PollingResult.Retry
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given status request data is null, then invoke result with PollingResult Failure")
    @Test
    fun invokePollingResultFailureOnDataNull() {
        every { statusCallResult.data } returns null

        runTest { sut.start() }

        val expectedResult = PollingResult.CallFailure()
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given status request failed, then invoke result with PollingResult Failure")
    @Test
    fun invokePollingResultFailureOnRequestFailed() {
        val failStatusCallResult: JudoApiCallResult.Failure = mockk(relaxed = true)
        coEvery {
            service.status("orderId").await().hint(JudoApiCallResult::class)
        } returns failStatusCallResult

        runTest { sut.start() }

        val expectedResult = PollingResult.CallFailure(error = failStatusCallResult.error)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given retry is called, then start polling")
    @Test
    fun startPollingOnRetry() {
        runTest {
            sut.start()
            sut.retry()
        }

        coVerify(exactly = 2) { service.status("orderId") }
    }
}
