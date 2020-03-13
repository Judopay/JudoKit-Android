package com.judopay.model

import com.judopay.R

enum class PaymentWidgetType {
    CARD_PAYMENT,
    PRE_AUTH_CARD_PAYMENT,
    CREATE_CARD_TOKEN,
    SAVE_CARD,
    CHECK_CARD,
    GOOGLE_PAY,
    PRE_AUTH_GOOGLE_PAY,
    PAYMENT_METHODS,
    PRE_AUTH_PAYMENT_METHODS
}

val PaymentWidgetType.navigationGraphId: Int
    @Throws(UnsupportedOperationException::class)
    get() = when (this) {
        PaymentWidgetType.CARD_PAYMENT,
        PaymentWidgetType.PRE_AUTH_CARD_PAYMENT,
        PaymentWidgetType.CREATE_CARD_TOKEN,
        PaymentWidgetType.SAVE_CARD,
        PaymentWidgetType.CHECK_CARD -> R.navigation.judo_card_input_graph
        PaymentWidgetType.PAYMENT_METHODS,
        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> R.navigation.judo_payment_methods_graph
        else -> throw UnsupportedOperationException("Payment Widget of Type: $this not supported")
    }

val PaymentWidgetType.isPaymentMethodsWidget: Boolean
    get() = this == PaymentWidgetType.PAYMENT_METHODS || this == PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS

val PaymentWidgetType.isCardPaymentWidget: Boolean
    get() = this == PaymentWidgetType.CARD_PAYMENT || this == PaymentWidgetType.PRE_AUTH_CARD_PAYMENT