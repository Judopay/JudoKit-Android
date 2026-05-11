package com.judopay.judokit.android

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.ui.common.parcelable

/**
 * A collection of [ActivityResultContract] implementations for launching Judo payment flows,
 * modeled after the [androidx.activity.result.contract.ActivityResultContracts] pattern.
 *
 * Each nested class corresponds to a [PaymentWidgetType] and determines the payment widget to
 * launch. Register the contract that matches the flow you want and pass a fully-built [Judo]
 * instance — no need to specify [PaymentWidgetType] in [Judo.Builder].
 *
 * ## Usage
 *
 * ```kotlin
 * val judo = Judo.Builder()
 *     .setJudoId("your-judo-id")
 *     // ...other configuration...
 *     .build()
 *
 * val paymentLauncher =
 *     registerForActivityResult(JudoActivityResultContracts.CardPayment()) { result ->
 *         when (result) {
 *             is JudoPaymentResult.Success -> handleSuccess(result.result)
 *             is JudoPaymentResult.Error -> handleError(result.error)
 *             is JudoPaymentResult.UserCancelled -> handleCancelled()
 *         }
 *     }
 *
 * paymentLauncher.launch(judo)
 * ```
 *
 * @see JudoPaymentResult
 * @see Judo
 */
class JudoActivityResultContracts private constructor() {
    /**
     * Base contract shared by all Judo payment widget contracts.
     */
    abstract class Base : ActivityResultContract<Judo, JudoPaymentResult>() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input)

        override fun parseResult(
            resultCode: Int,
            intent: Intent?,
        ): JudoPaymentResult =
            when (resultCode) {
                PAYMENT_SUCCESS -> {
                    val result = intent?.parcelable<JudoResult>(JUDO_RESULT)
                    if (result != null) {
                        JudoPaymentResult.Success(result)
                    } else {
                        JudoPaymentResult.Error(JudoError.judoInternalError("Missing result in success response"))
                    }
                }
                PAYMENT_ERROR -> {
                    val error =
                        intent?.parcelable<JudoError>(JUDO_ERROR)
                            ?: JudoError.judoInternalError("Missing error details in error response")
                    JudoPaymentResult.Error(error)
                }
                else -> {
                    val error =
                        intent?.parcelable<JudoError>(JUDO_ERROR)
                            ?: JudoError.userCancelled()
                    JudoPaymentResult.UserCancelled(error)
                }
            }
    }

    /**
     * Contract for [PaymentWidgetType.CARD_PAYMENT]: launches the standard card payment flow.
     */
    class CardPayment : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.CARD_PAYMENT))
    }

    /**
     * Contract for [PaymentWidgetType.PRE_AUTH]: launches the pre-authorization card payment flow.
     */
    class CardPreAuth : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.PRE_AUTH))
    }

    /**
     * Contract for [PaymentWidgetType.CREATE_CARD_TOKEN]: launches the save-card flow for future
     * tokenized payments.
     */
    class CreateCardToken : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.CREATE_CARD_TOKEN))
    }

    /**
     * Contract for [PaymentWidgetType.CHECK_CARD]: launches the card check/verification flow.
     */
    class CheckCard : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.CHECK_CARD))
    }

    /**
     * Contract for [PaymentWidgetType.GOOGLE_PAY]: launches a standalone GooglePay payment flow.
     */
    class GooglePay : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.GOOGLE_PAY))
    }

    /**
     * Contract for [PaymentWidgetType.PRE_AUTH_GOOGLE_PAY]: launches a standalone GooglePay
     * pre-authorization flow.
     */
    class PreAuthGooglePay : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.PRE_AUTH_GOOGLE_PAY))
    }

    /**
     * Contract for [PaymentWidgetType.PAYMENT_METHODS]: launches the payment methods widget
     * (card and GooglePay).
     */
    class PaymentMethods : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.PAYMENT_METHODS))
    }

    /**
     * Contract for [PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS]: launches the payment methods
     * widget for pre-authorization (card and GooglePay).
     */
    class PreAuthPaymentMethods : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS))
    }

    /**
     * Contract for [PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS]: launches the
     * server-to-server payment methods widget to create a receipt for card payments made outside
     * the Judo SDK.
     */
    class ServerToServerPaymentMethods : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS))
    }

    /**
     * Contract for [PaymentWidgetType.TOKEN_PAYMENT]: launches a token payment flow, optionally
     * prompting the user for their CSC and/or cardholder name.
     */
    class CardTokenPayment : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.TOKEN_PAYMENT))
    }

    /**
     * Contract for [PaymentWidgetType.TOKEN_PRE_AUTH]: launches a pre-authorization token payment
     * flow, optionally prompting the user for their CSC and/or cardholder name.
     */
    class CardTokenPreAuth : Base() {
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent = JudoActivity.createIntent(context, input.withPaymentWidgetType(PaymentWidgetType.TOKEN_PRE_AUTH))
    }
}
