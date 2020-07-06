package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.R
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class PaymentWidgetType : Parcelable {
    CARD_PAYMENT,
    PRE_AUTH,
    REGISTER_CARD,
    CREATE_CARD_TOKEN,
    CHECK_CARD,
    GOOGLE_PAY,
    PRE_AUTH_GOOGLE_PAY,
    PAYMENT_METHODS,
    PRE_AUTH_PAYMENT_METHODS,
    SERVER_TO_SERVER_PAYMENT_METHODS,
    PAY_BY_BANK_APP
}

val PaymentWidgetType.navigationGraphId: Int
    @Throws(UnsupportedOperationException::class)
    get() = when (this) {
        PaymentWidgetType.CARD_PAYMENT,
        PaymentWidgetType.PRE_AUTH,
        PaymentWidgetType.REGISTER_CARD,
        PaymentWidgetType.CREATE_CARD_TOKEN,
        PaymentWidgetType.CHECK_CARD -> R.navigation.judo_card_input_graph
        PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
        PaymentWidgetType.PAYMENT_METHODS,
        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> R.navigation.judo_payment_methods_graph
        PaymentWidgetType.PAY_BY_BANK_APP -> R.navigation.judo_polling_status_graph
        else -> throw UnsupportedOperationException("Payment Widget of Type: $this not supported")
    }

val PaymentWidgetType.isPaymentMethodsWidget: Boolean
    get() = this == PaymentWidgetType.PAYMENT_METHODS || this == PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS || this == PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS

val PaymentWidgetType.isCardPaymentWidget: Boolean
    get() = this == PaymentWidgetType.CARD_PAYMENT || this == PaymentWidgetType.PRE_AUTH

val PaymentWidgetType.isGooglePayWidget: Boolean
    get() = this == PaymentWidgetType.GOOGLE_PAY || this == PaymentWidgetType.PRE_AUTH_GOOGLE_PAY

val PaymentWidgetType.isExposed: Boolean
    get() = this == PaymentWidgetType.PAY_BY_BANK_APP
