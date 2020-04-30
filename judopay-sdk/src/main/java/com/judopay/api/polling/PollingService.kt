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
    val data: String,
    val service: JudoApiService,
    val result: (PollingResult<JudoApiCallResult<BankSaleStatusResponse>>) -> Unit
) {

    suspend fun start() {
        var timeout = TIMEOUT
        while (timeout != 0L) {
            if (timeout != DELAY_IN_SECONDS * MILLISECONDS) {
                delay(REQUEST_DELAY)
            }
            when (val saleStatusResponse = service.status(data)) {
                is JudoApiCallResult.Success -> {
                    if (saleStatusResponse.data != null)
                        when (saleStatusResponse.data.orderDetails.orderStatus) {
                            OrderStatus.SUCCEEDED -> {
                                timeout = 0L
                                result.invoke(PollingResult.Success(saleStatusResponse))
                            }
                            OrderStatus.PENDING -> {
                                timeout -= REQUEST_DELAY
                                if (timeout <= TIMEOUT / 2) {
                                    result.invoke(PollingResult.Delay(saleStatusResponse))
                                }
                                if (timeout == 0L)
                                    result.invoke(PollingResult.Failure(saleStatusResponse))
                            }
                            else -> {
                                timeout = 0L
                                result.invoke(PollingResult.Failure(saleStatusResponse))
                            }
                        }
                }
                is JudoApiCallResult.Failure -> {
                    timeout = 0L
                    result.invoke(PollingResult.Failure(saleStatusResponse))
                }
            }
        }
    }
}
