package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.R
import kotlinx.android.parcel.Parcelize

/**
 * A set of values that is used to invoke any of the defined payment flows. It is a mandatory
 * parameter when creating a [com.judokit.android.Judo] configuration object through it's builder.
 * @see com.judokit.android.Judo.Builder.paymentWidgetType
 */
@Parcelize
enum class PaymentWidgetType : Parcelable {
    /**
     * Starts a standard card payment flow.
     */
    CARD_PAYMENT,

    /**
     * Starts a pre-auth card payment flow.
     */
    PRE_AUTH,

    /**
     * Starts a register card flow for making future tokenized payments
     */
    REGISTER_CARD,

    /**
     * Starts a save card flow for making future tokenized payments
     */
    CREATE_CARD_TOKEN,

    /**
     * Starts a check card flow to perform a card check against the card.
     */
    CHECK_CARD,

    /**
     * Starts a GooglePay payment flow outside of payment methods screen.
     */
    GOOGLE_PAY,

    /**
     * Starts a pre-auth GooglePay payment flow outside of payment methods screen.
     */
    PRE_AUTH_GOOGLE_PAY,

    /**
     * Starts the payment methods widget to perform card, GooglePay, Pay by Bank app and iDEAL
     * payments.
     */
    PAYMENT_METHODS,

    /**
     * Starts the payment methods widget to perform card, GooglePay, Pay by Bank app and iDEAL
     * pre-auth payments.
     */
    PRE_AUTH_PAYMENT_METHODS,

    /**
     * Starts the payment methods widget to create a receipt against a card which can be used to
     * make payments outside of Judo SDK. Works for card payments only.
     */
    SERVER_TO_SERVER_PAYMENT_METHODS,

    /**
     * Starts Pay by Bank app payment flow outside of payment method screen. You must set GBP
     * currency [com.judokit.android.Judo.Builder.setAmount] when building Judo configuration object
     * to be able to start Pay by Bank app journey.
     */
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
