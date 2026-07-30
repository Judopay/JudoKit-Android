package com.judopay.judokit.android.model

import android.os.Parcelable
import com.judopay.judokit.android.model.googlepay.GooglePayBillingAddressParameters
import com.judopay.judokit.android.model.googlepay.GooglePayCheckoutOption
import com.judopay.judokit.android.model.googlepay.GooglePayDeferredParameters
import com.judopay.judokit.android.model.googlepay.GooglePayEnvironment
import com.judopay.judokit.android.model.googlepay.GooglePayPriceStatus
import com.judopay.judokit.android.model.googlepay.GooglePayRecurringParameters
import com.judopay.judokit.android.model.googlepay.GooglePayShippingAddressParameters
import com.judopay.judokit.android.requireNotNull
import kotlinx.parcelize.Parcelize
import java.util.Locale

/**
 * A configuration class responsible for setting all the necessary parameters for a
 * GooglePay transaction.
 */
@Parcelize
@Suppress("LongParameterList")
class GooglePayConfiguration internal constructor(
    val environment: GooglePayEnvironment,
    val merchantName: String?,
    val transactionCountryCode: String,
    val transactionId: String?,
    val totalPriceStatus: GooglePayPriceStatus,
    val totalPriceLabel: String?,
    val checkoutOption: GooglePayCheckoutOption?,
    val isEmailRequired: Boolean?,
    val isBillingAddressRequired: Boolean,
    val billingAddressParameters: GooglePayBillingAddressParameters?,
    val isShippingAddressRequired: Boolean,
    val shippingAddressParameters: GooglePayShippingAddressParameters?,
    val allowPrepaidCards: Boolean?,
    val allowCreditCards: Boolean?,
    val deferredParameters: GooglePayDeferredParameters?,
    val recurringParameters: GooglePayRecurringParameters?,
) : Parcelable {
    @Suppress("TooManyFunctions")
    class Builder {
        private var environment: GooglePayEnvironment? = null
        private var merchantName: String? = null

        private var transactionCountryCode: String? = null
        private var transactionId: String? = null

        private var totalPriceStatus: GooglePayPriceStatus? = null
        private var totalPriceLabel: String? = null

        private var checkoutOption: GooglePayCheckoutOption? = null

        private var isEmailRequired: Boolean? = null

        private var isBillingAddressRequired: Boolean? = null
        private var billingAddressParameters: GooglePayBillingAddressParameters? = null

        private var isShippingAddressRequired: Boolean? = null
        private var shippingAddressParameters: GooglePayShippingAddressParameters? = null

        private var allowPrepaidCards: Boolean? = null
        private var allowCreditCards: Boolean? = null

        private var deferredParameters: GooglePayDeferredParameters? = null
        private var recurringParameters: GooglePayRecurringParameters? = null

        /**
         * Sets the environment.
         * @see GooglePayEnvironment
         */
        fun setEnvironment(environment: GooglePayEnvironment?) = apply { this.environment = environment }

        /**
         * Sets the merchant name.
         */
        fun setMerchantName(name: String?) = apply { this.merchantName = name }

        /**
         * Sets the transaction country code.
         */
        fun setTransactionCountryCode(countryCode: String?) = apply { this.transactionCountryCode = countryCode }

        /**
         * Sets the transaction identifier.
         */
        fun setTransactionId(id: String?) = apply { this.transactionId = id }

        /**
         * Sets the total price status.
         * If set to null, [GooglePayPriceStatus.FINAL] will be used.
         * @see GooglePayPriceStatus
         */
        fun setTotalPriceStatus(status: GooglePayPriceStatus?) = apply { this.totalPriceStatus = status }

        /**
         * Sets the total price label.
         */
        fun setTotalPriceLabel(totalLabel: String?) = apply { this.totalPriceLabel = totalLabel }

        /**
         * Sets the checkout option.
         * @see GooglePayCheckoutOption
         */
        fun setCheckoutOption(option: GooglePayCheckoutOption?) = apply { this.checkoutOption = option }

        /**
         * Sets whether the email is required.
         */
        fun setIsEmailRequired(required: Boolean?) = apply { this.isEmailRequired = required }

        /**
         * Sets whether the billing address is required.
         */
        fun setIsBillingAddressRequired(required: Boolean?) = apply { this.isBillingAddressRequired = required }

        /**
         * Sets the billing address parameters.
         * @see GooglePayBillingAddressParameters
         */
        fun setBillingAddressParameters(parameters: GooglePayBillingAddressParameters?) =
            apply { this.billingAddressParameters = parameters }

        /**
         * Sets whether the shipping address is required.
         */
        fun setIsShippingAddressRequired(required: Boolean?) = apply { this.isShippingAddressRequired = required }

        /**
         * Sets shipping address parameters.
         * @see GooglePayShippingAddressParameters
         */
        fun setShippingAddressParameters(parameters: GooglePayShippingAddressParameters?) =
            apply { this.shippingAddressParameters = parameters }

        /*
         * Set to false if you don't support prepaid cards.
         */
        fun setAllowPrepaidCards(allowPrepaidCards: Boolean?) = apply { this.allowPrepaidCards = allowPrepaidCards }

        /*
         * Set to false if you don't support credit cards.
         */
        fun setAllowCreditCards(allowCreditCards: Boolean?) = apply { this.allowCreditCards = allowCreditCards }

        /**
         * Sets Deferred MIT parameters.
         * @see GooglePayDeferredParameters
         */
        fun setDeferredParameters(parameters: GooglePayDeferredParameters?) = apply { this.deferredParameters = parameters }

        /**
         * Sets Recurring MIT parameters.
         * @see GooglePayRecurringParameters
         */
        fun setRecurringParameters(parameters: GooglePayRecurringParameters?) = apply { this.recurringParameters = parameters }

        /**
         * Creates an instance of [GooglePayConfiguration] based on provided data in setters.
         * @throws IllegalArgumentException If environment or country code is null.
         * @throws IllegalArgumentException If any of the allowedCountryCodes are invalid.
         * @throws IllegalArgumentException If more than one MIT parameter object is set.
         * @return An instance of [GooglePayConfiguration]
         */
        fun build(): GooglePayConfiguration {
            val env = requireNotNull(environment, "environment")
            val countryCode = requireNotNull(transactionCountryCode, "transactionCountryCode")

            shippingAddressParameters?.let { parameters ->
                val countryCodes = Locale.getISOCountries()
                parameters.allowedCountryCodes?.forEach { allowedCode ->
                    require(countryCodes.contains(allowedCode)) {
                        "'$allowedCode' is not a valid country code"
                    }
                }
            }

            val mitParameterCount = listOfNotNull(deferredParameters, recurringParameters).size
            require(mitParameterCount <= 1) {
                "Only one of deferredParameters or recurringParameters may be set"
            }

            recurringParameters?.let { parameters ->
                require(parameters.recurrenceItems.isNotEmpty()) {
                    "recurringParameters.recurrenceItems must not be empty"
                }
            }

            return GooglePayConfiguration(
                environment = env,
                merchantName = merchantName,
                transactionCountryCode = countryCode,
                transactionId = transactionId,
                totalPriceStatus =
                    totalPriceStatus
                        ?: GooglePayPriceStatus.FINAL,
                totalPriceLabel = totalPriceLabel,
                checkoutOption = checkoutOption,
                isEmailRequired = isEmailRequired,
                isBillingAddressRequired = isBillingAddressRequired ?: billingAddressParameters != null,
                billingAddressParameters = billingAddressParameters,
                isShippingAddressRequired = isShippingAddressRequired ?: shippingAddressParameters != null,
                shippingAddressParameters = shippingAddressParameters,
                allowPrepaidCards = allowPrepaidCards,
                allowCreditCards = allowCreditCards,
                deferredParameters = deferredParameters,
                recurringParameters = recurringParameters,
            )
        }
    }
}
