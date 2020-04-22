package com.judopay.api.model.response

import com.judopay.model.JudoResult
import com.judopay.ui.common.toDate
import java.util.Locale

data class IdealSaleStatusResponse(
    val paymentMethod: String,
    val siteId: String,
    val orderDetails: OrderDetails,
    val merchantPaymentReference: String,
    val merchantConsumerReference: String
)

fun IdealSaleStatusResponse.toJudoResult(locale: Locale) = JudoResult(
    receiptId = orderDetails.orderId,
    result = orderDetails.orderStatus.name,
    createdAt = toDate(orderDetails.timestamp, locale),
    currency = orderDetails.currency,
    amount = orderDetails.amount,
    yourPaymentReference = merchantPaymentReference,
    consumer = Consumer(yourConsumerReference = merchantConsumerReference)
)
