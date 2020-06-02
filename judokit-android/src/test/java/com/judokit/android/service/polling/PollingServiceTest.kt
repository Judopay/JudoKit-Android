package com.judokit.android.service.polling

import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.OrderStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName

@ExperimentalCoroutinesApi
@DisplayName("Testing polling logic")
internal class PollingServiceTest {

    private val service: JudoApiService = mockk(relaxed = true)
    private lateinit var actualResult: PollingResult<BankSaleStatusResponse>

    private val sut = PollingService(service).apply {
        orderId = "orderId"
        result = { actualResult = it }
    }

    private val statusResponse = mockk<BankSaleStatusResponse>(relaxed = true)
    private val statusCallResult: JudoApiCallResult.Success<BankSaleStatusResponse> =
        mockk(relaxed = true) {
            every { data } returns statusResponse
        }

    @Before
    fun setUp() {
        coEvery {
            service.status("orderId").hint(JudoApiCallResult::class)
        } returns statusCallResult
    }

    @DisplayName("Given polling starts, when timeout>0, then make status call")
    @Test
    fun makeStatusCallWhenPollingStarts() {
        runBlockingTest { sut.start() }

        coVerify { service.status("orderId") }
    }

    @DisplayName("Given status call is successful, when data is not null, invoke result")
    @Test
    fun invokeResultWhenStatusSuccessful() {
        runBlockingTest { sut.start() }

        val expectedResult = PollingResult.Success(statusResponse)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given order status is SUCCEEDED, then invoke result with PollingResult SUCCEEDED")
    @Test
    fun invokePollingResultSuccessOnOrderStatusSuccess() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.SUCCEEDED

        runBlockingTest { sut.start() }

        val expectedResult = PollingResult.Success(statusResponse)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given order status is FAILED, then invoke result with PollingResult FAILED")
    @Test
    fun invokePollingResultSuccessOnOrderStatusFailed() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.FAILED

        runBlockingTest { sut.start() }

        val expectedResult = PollingResult.Success(statusResponse)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given order status is PENDING, then invoke result with PollingResult Retry")
    @Test
    fun invokePollingResultProcessingOnOrderStatusPending() {
        every { statusResponse.orderDetails.orderStatus } returns OrderStatus.PENDING

        runBlockingTest { sut.start() }

        val expectedResult = PollingResult.Retry
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given status request data is null, then invoke result with PollingResult Failure")
    @Test
    fun invokePollingResultFailureOnDataNull() {
        every { statusCallResult.data } returns null

        runBlockingTest { sut.start() }

        val expectedResult = PollingResult.CallFailure()
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given status request failed, then invoke result with PollingResult Failure")
    @Test
    fun invokePollingResultFailureOnRequestFailed() {
        val failStatusCallResult: JudoApiCallResult.Failure = mockk(relaxed = true)
        coEvery {
            service.status("orderId").hint(JudoApiCallResult::class)
        } returns failStatusCallResult

        runBlockingTest { sut.start() }

        val expectedResult = PollingResult.CallFailure(error = failStatusCallResult.error)
        assertEquals(expectedResult, actualResult)
    }

    @DisplayName("Given retry is called, then start polling")
    @Test
    fun startPollingOnRetry() {
        runBlockingTest {
            sut.start()
            sut.retry()
        }

        coVerify(exactly = 2) { service.status("orderId") }
    }
}
