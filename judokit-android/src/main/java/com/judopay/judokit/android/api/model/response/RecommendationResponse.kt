package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.JudoResult
import java.util.Locale

data class RecommendationResponse(
    val data: RecommendationData
)

fun RecommendationResponse.toJudoResult(locale: Locale) = JudoResult(
//    receiptId = orderDetails.orderId,
//    result = orderDetails.orderStatus.name,
//    createdAt = toDate(orderDetails.timestamp, locale),
//    currency = orderDetails.currency,
//    amount = orderDetails.amount,
//    yourPaymentReference = merchantPaymentReference,
//    consumer = Consumer(yourConsumerReference = merchantConsumerReference)
)
