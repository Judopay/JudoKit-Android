package com.judokit.android.api.model.response

import com.judokit.android.model.JudoResult
import com.judokit.android.ui.common.toDate
import java.util.Locale

data class BankSaleStatusResponse(
    val paymentMethod: String,
    val siteId: String,
    val orderDetails: OrderDetails,
    val merchantPaymentReference: String,
    val merchantConsumerReference: String
)

fun BankSaleStatusResponse.toJudoResult(locale: Locale) = JudoResult(
    receiptId = orderDetails.orderId,
    result = orderDetails.orderStatus.name,
    createdAt = toDate(orderDetails.timestamp, locale),
    currency = orderDetails.currency,
    amount = orderDetails.amount,
    yourPaymentReference = merchantPaymentReference,
    consumer = Consumer(yourConsumerReference = merchantConsumerReference)
)
