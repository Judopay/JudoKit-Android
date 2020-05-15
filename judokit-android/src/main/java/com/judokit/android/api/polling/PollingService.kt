package com.judopay.api.polling

import com.judopay.api.JudoApiService
import com.judopay.api.model.response.BankSaleStatusResponse
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.OrderStatus
import kotlinx.coroutines.delay

private const val DELAY_IN_SECONDS = 130L
private const val MILLISECONDS = 1000L
private const val REQUEST_DELAY = 5000L
private const val TIMEOUT = DELAY_IN_SECONDS * MILLISECONDS

class PollingService(
    private val data: String,
    private val service: JudoApiService,
    private val result: (PollingResult<BankSaleStatusResponse>) -> Unit
) {

    var timeout = TIMEOUT

    suspend fun start() {
        while (timeout > 0L) {
            if (timeout != DELAY_IN_SECONDS * MILLISECONDS) {
                delay(REQUEST_DELAY)
            }
            when (val saleStatusResponse = service.status(data)) {
                is JudoApiCallResult.Success -> {
                    if (saleStatusResponse.data != null)
                        when (saleStatusResponse.data.orderDetails.orderStatus) {
                            OrderStatus.SUCCEEDED -> {
                                timeout = 0L
                                result.invoke(PollingResult.Success(saleStatusResponse.data))
                            }
                            OrderStatus.PENDING -> {
                                timeout -= REQUEST_DELAY
                                when {
                                    timeout <= 0L -> result.invoke(PollingResult.Retry)
                                    timeout <= TIMEOUT / 2 -> result.invoke(PollingResult.Delay)
                                    else -> result.invoke(PollingResult.Processing)
                                }
                            }
                            OrderStatus.FAILED -> {
                                timeout = 0L
                                result.invoke(PollingResult.Success(saleStatusResponse.data))
                            }
                        }
                }
                is JudoApiCallResult.Failure -> {
                    timeout = 0L
                    result.invoke(PollingResult.Failure(error = saleStatusResponse.error))
                }
            }
        }
    }

    // restarts the polling flow
    suspend fun retry() {
        timeout = TIMEOUT
        start()
    }

    // resets the timer while polling is in progress
    fun reset() {
        timeout = TIMEOUT
    }

    // Sets the timer to 0 to exit polling
    fun cancel() {
        timeout = 0L
    }
}
