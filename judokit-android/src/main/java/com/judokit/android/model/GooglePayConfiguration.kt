package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.model.googlepay.GooglePayBillingAddressParameters
import com.judokit.android.model.googlepay.GooglePayCheckoutOption
import com.judokit.android.model.googlepay.GooglePayEnvironment
import com.judokit.android.model.googlepay.GooglePayPriceStatus
import com.judokit.android.model.googlepay.GooglePayShippingAddressParameters
import com.judokit.android.requireNotNull
import kotlinx.android.parcel.Parcelize
import java.util.Locale

@Parcelize
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
    val shippingAddressParameters: GooglePayShippingAddressParameters?
) : Parcelable {

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

        fun setEnvironment(environment: GooglePayEnvironment?) =
            apply { this.environment = environment }

        fun setMerchantName(name: String?) = apply { this.merchantName = name }

        fun setTransactionCountryCode(countryCode: String?) =
            apply { this.transactionCountryCode = countryCode }

        fun setTransactionId(id: String?) = apply { this.transactionId = id }

        fun setTotalPriceStatus(status: GooglePayPriceStatus?) =
            apply { this.totalPriceStatus = status }

        fun setTotalPriceLabel(totalLabel: String?) = apply { this.totalPriceLabel = totalLabel }

        fun setCheckoutOption(option: GooglePayCheckoutOption?) =
            apply { this.checkoutOption = option }

        fun setIsEmailRequired(required: Boolean?) = apply { this.isEmailRequired = required }

        fun setIsBillingAddressRequired(required: Boolean?) =
            apply { this.isBillingAddressRequired = required }

        fun setBillingAddressParameters(parameters: GooglePayBillingAddressParameters?) =
            apply { this.billingAddressParameters = parameters }

        fun setIsShippingAddressRequired(required: Boolean?) =
            apply { this.isShippingAddressRequired = required }

        fun setShippingAddressParameters(parameters: GooglePayShippingAddressParameters?) =
            apply { this.shippingAddressParameters = parameters }

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

            return GooglePayConfiguration(
                environment = env,
                merchantName = merchantName,
                transactionCountryCode = countryCode,
                transactionId = transactionId,
                totalPriceStatus = totalPriceStatus
                    ?: GooglePayPriceStatus.FINAL,
                totalPriceLabel = totalPriceLabel,
                checkoutOption = checkoutOption,
                isEmailRequired = isEmailRequired,
                isBillingAddressRequired = isBillingAddressRequired ?: billingAddressParameters != null,
                billingAddressParameters = billingAddressParameters,
                isShippingAddressRequired = isShippingAddressRequired
                    ?: shippingAddressParameters != null,
                shippingAddressParameters = shippingAddressParameters
            )
        }
    }
}
