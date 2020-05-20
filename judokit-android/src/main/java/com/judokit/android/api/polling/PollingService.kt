package com.judokit.android.api.polling

import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.OrderStatus
import kotlinx.coroutines.delay

private const val DELAY_IN_SECONDS = 130L
private const val MILLISECONDS = 1000L
private const val REQUEST_DELAY = 5000L
private const val TIMEOUT = DELAY_IN_SECONDS * MILLISECONDS

class PollingService(
    private val orderId: String,
    private val service: JudoApiService,
    private val result: (PollingResult<BankSaleStatusResponse>) -> Unit
) {

    var timeout = TIMEOUT

    suspend fun start() {
        while (timeout > 0L) {
            if (timeout != DELAY_IN_SECONDS * MILLISECONDS) {
                delay(REQUEST_DELAY)
            }
            when (val saleStatusResponse = service.status(orderId)) {
                is JudoApiCallResult.Success -> {
                    if (saleStatusResponse.data != null) {
                        handleOrderStatus(saleStatusResponse.data, saleStatusResponse)
                    } else {
                        timeout = 0L
                        result.invoke(PollingResult.Failure())
                    }
                }
                is JudoApiCallResult.Failure -> {
                    timeout = 0L
                    result.invoke(PollingResult.Failure(error = saleStatusResponse.error))
                }
            }
        }
    }

    private fun handleOrderStatus(
        data: BankSaleStatusResponse,
        saleStatusResponse: JudoApiCallResult.Success<BankSaleStatusResponse>
    ) {
        when (data.orderDetails.orderStatus) {
            OrderStatus.SUCCEEDED,
            OrderStatus.FAILED -> {
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
