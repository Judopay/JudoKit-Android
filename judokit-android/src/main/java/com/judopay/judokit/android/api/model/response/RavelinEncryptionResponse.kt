package com.judopay.judokit.android.api.model.response

import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.ui.common.toDate
import java.util.Locale

data class RavelinEncryptionResponse(
    val data: RavelinEncryptionData
)

fun RavelinEncryptionResponse.toJudoResult(locale: Locale) = JudoResult(
//    receiptId = orderDetails.orderId,
//    result = orderDetails.orderStatus.name,
//    createdAt = toDate(orderDetails.timestamp, locale),
//    currency = orderDetails.currency,
//    amount = orderDetails.amount,
//    yourPaymentReference = merchantPaymentReference,
//    consumer = Consumer(yourConsumerReference = merchantConsumerReference)
)
