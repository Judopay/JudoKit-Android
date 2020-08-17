package com.judokit.android.api.model.response

import com.judokit.android.model.JudoResult
import java.math.BigDecimal

data class BankSaleResponse(
    val orderId: String,
    val paymentMethod: String,
    val status: String,
    val merchantPaymentReference: String,
    val currency: String,
    val amount: BigDecimal,
    val consumer: BankConsumer,
    val judoId: String,
    val merchantSiteName: String,
    val redirectUrl: String,
    val merchantRedirectUrl: String,
    val merchantPaymentMetadata: String,
    val pbbaBrn: String,
    val secureToken: String
)

fun BankSaleResponse.toJudoResult() = JudoResult(
    receiptId = orderId,
    result = status,
    currency = currency,
    amount = amount,
    yourPaymentReference = merchantPaymentReference
)
