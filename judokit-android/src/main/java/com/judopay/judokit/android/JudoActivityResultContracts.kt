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
 * Each nested class corresponds to a [PaymentWidgetType] and is responsible for creating the
 * correct launch [Intent] and parsing the activity result into a [JudoPaymentResult].
 *
 * Register the contract that matches the [PaymentWidgetType] set on your [Judo] configuration
 * object. All contracts accept a fully-built [Judo] instance and return a [JudoPaymentResult].
 *
 * ## Usage
 *
 * ```kotlin
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
 * @see PaymentWidgetType
 */
class JudoActivityResultContracts private constructor() {
    /**
     * Base contract shared by all Judo payment widget contracts.
     *
     * Subclass this to create a custom contract for a specific [PaymentWidgetType], or extend
     * one of the concrete nested classes (e.g. [CardPayment]) to override behaviour for
     * a particular flow.
     */
    abstract class Base : ActivityResultContract<Judo, JudoPaymentResult>() {
        @Suppress("DEPRECATION")
        override fun createIntent(
            context: Context,
            input: Judo,
        ): Intent =
            Intent(context, JudoActivity::class.java).apply {
                putExtra(JUDO_OPTIONS, input)
            }

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
    open class CardPayment : Base()

    /**
     * Contract for [PaymentWidgetType.PRE_AUTH]: launches the pre-authorization card payment flow.
     */
    open class CardPreAuth : Base()

    /**
     * Contract for [PaymentWidgetType.CREATE_CARD_TOKEN]: launches the save-card flow for future
     * tokenized payments.
     */
    open class CreateCardToken : Base()

    /**
     * Contract for [PaymentWidgetType.CHECK_CARD]: launches the card check/verification flow.
     */
    open class CheckCard : Base()

    /**
     * Contract for [PaymentWidgetType.GOOGLE_PAY]: launches a standalone GooglePay payment flow.
     */
    open class GooglePay : Base()

    /**
     * Contract for [PaymentWidgetType.PRE_AUTH_GOOGLE_PAY]: launches a standalone GooglePay
     * pre-authorization flow.
     */
    open class PreAuthGooglePay : Base()

    /**
     * Contract for [PaymentWidgetType.PAYMENT_METHODS]: launches the payment methods widget
     * (card and GooglePay).
     */
    open class PaymentMethods : Base()

    /**
     * Contract for [PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS]: launches the payment methods
     * widget for pre-authorization (card and GooglePay).
     */
    open class PreAuthPaymentMethods : Base()

    /**
     * Contract for [PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS]: launches the
     * server-to-server payment methods widget to create a receipt for card payments made outside
     * the Judo SDK.
     */
    open class ServerToServerPaymentMethods : Base()

    /**
     * Contract for [PaymentWidgetType.TOKEN_PAYMENT]: launches a token payment flow, optionally
     * prompting the user for their CSC and/or cardholder name.
     */
    open class CardTokenPayment : Base()

    /**
     * Contract for [PaymentWidgetType.TOKEN_PRE_AUTH]: launches a pre-authorization token payment
     * flow, optionally prompting the user for their CSC and/or cardholder name.
     */
    open class CardTokenPreAuth : Base()
}
