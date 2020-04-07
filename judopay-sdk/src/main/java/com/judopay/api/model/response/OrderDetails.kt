package com.judopay.api.model.response

import java.math.BigDecimal

data class OrderDetails(
    val orderId: String,
    val orderStatus: OrderStatus,
    val timestamp: String,
    val currency: String,
    val amount: BigDecimal,
    val refundedAmount: BigDecimal
)
