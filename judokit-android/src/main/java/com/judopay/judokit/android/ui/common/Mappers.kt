package com.judopay.judokit.android.ui.common

import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.model.request.GooglePayRequest
import com.judopay.judokit.android.api.model.request.GooglePayWallet
import com.judopay.judokit.android.api.model.request.PreAuthGooglePayRequest
import com.judopay.judokit.android.model.GooglePayConfiguration
import com.judopay.judokit.android.model.googlepay.GPayPaymentGatewayParameters
import com.judopay.judokit.android.model.googlepay.GooglePayAuthMethod
import com.judopay.judokit.android.model.googlepay.GooglePayCardParameters
import com.judopay.judokit.android.model.googlepay.GooglePayDeferredTransactionInfo
import com.judopay.judokit.android.model.googlepay.GooglePayIsReadyToPayRequest
import com.judopay.judokit.android.model.googlepay.GooglePayMerchantInfo
import com.judopay.judokit.android.model.googlepay.GooglePayPaymentData
import com.judopay.judokit.android.model.googlepay.GooglePayPaymentDataRequest
import com.judopay.judokit.android.model.googlepay.GooglePayPaymentMethod
import com.judopay.judokit.android.model.googlepay.GooglePayPaymentMethodTokenizationSpecification
import com.judopay.judokit.android.model.googlepay.GooglePayPaymentMethodType
import com.judopay.judokit.android.model.googlepay.GooglePayRecurringTransactionInfo
import com.judopay.judokit.android.model.googlepay.GooglePayTokenizationSpecificationType
import com.judopay.judokit.android.model.googlepay.GooglePayTransactionInfo
import com.judopay.judokit.android.model.isSupportedByGooglePay
import com.judopay.judokit.android.toJSONString
import com.judopay.judokit.android.toMap

internal fun GooglePayConfiguration.toGooglePayPaymentMethod(judo: Judo): GooglePayPaymentMethod {
    val networks = judo.supportedCardNetworks.filter { it.isSupportedByGooglePay }

    val cardParameters =
        GooglePayCardParameters(
            allowedAuthMethods =
                arrayOf(
                    GooglePayAuthMethod.CRYPTOGRAM_3DS,
                ),
            allowedCardNetworks = networks.toTypedArray(),
            allowPrepaidCards = allowPrepaidCards,
            allowCreditCards = allowCreditCards,
            billingAddressRequired = isBillingAddressRequired,
            billingAddressParameters = billingAddressParameters,
        )

    val tokenizationSpecification =
        GooglePayPaymentMethodTokenizationSpecification(
            type = GooglePayTokenizationSpecificationType.PAYMENT_GATEWAY,
            parameters = GPayPaymentGatewayParameters(gatewayMerchantId = judo.judoId),
        )

    return GooglePayPaymentMethod(
        type = GooglePayPaymentMethodType.CARD,
        parameters = cardParameters,
        tokenizationSpecification = tokenizationSpecification,
    )
}

internal fun GooglePayConfiguration.toIsReadyToPayRequest(judo: Judo): IsReadyToPayRequest {
    val cardPaymentMethod = toGooglePayPaymentMethod(judo)

    val isReadyToPayRequest =
        GooglePayIsReadyToPayRequest(
            apiVersion = GOOGLE_PAY_API_VERSION,
            apiVersionMinor = GOOGLE_PAY_API_VERSION_MINOR,
            allowedPaymentMethods = arrayOf(cardPaymentMethod),
        )

    val json = isReadyToPayRequest.toJSONString()
    return IsReadyToPayRequest.fromJson(json)
}

internal fun GooglePayConfiguration.toPaymentDataRequest(judo: Judo): PaymentDataRequest {
    val json = toGooglePayPaymentDataRequest(judo).toJSONString()
    return PaymentDataRequest.fromJson(json)
}

internal fun GooglePayConfiguration.toGooglePayPaymentDataRequest(judo: Judo): GooglePayPaymentDataRequest {
    val currency = judo.amount.currency.name
    val recurringTransactionInfo = toRecurringTransactionInfo(currency)
    val deferredTransactionInfo = toDeferredTransactionInfo(currency)
    val transactionInfo =
        if (recurringTransactionInfo != null || deferredTransactionInfo != null) {
            null
        } else {
            toTransactionInfo(judo.amount.amount, currency)
        }

    return GooglePayPaymentDataRequest(
        apiVersion = GOOGLE_PAY_API_VERSION,
        apiVersionMinor = GOOGLE_PAY_API_VERSION_MINOR,
        merchantInfo = GooglePayMerchantInfo(merchantName),
        allowedPaymentMethods = arrayOf(toGooglePayPaymentMethod(judo)),
        transactionInfo = transactionInfo,
        emailRequired = isEmailRequired,
        shippingAddressRequired = isShippingAddressRequired,
        shippingAddressParameters = shippingAddressParameters,
        recurringTransactionInfo = recurringTransactionInfo,
        deferredTransactionInfo = deferredTransactionInfo,
    )
}

private fun GooglePayConfiguration.toRecurringTransactionInfo(currency: String) =
    recurringParameters?.let { parameters ->
        GooglePayRecurringTransactionInfo(
            currencyCode = currency,
            countryCode = transactionCountryCode,
            transactionId = transactionId,
            managementUrl = parameters.managementUrl,
            billingAgreement = parameters.billingAgreement,
            immediateTotalPrice = parameters.immediateTotalPrice,
            immediateDisplayItems = parameters.immediateDisplayItems,
            introductoryPeriodInfo = parameters.introductoryPeriodInfo,
            recurrenceItems = parameters.recurrenceItems,
        )
    }

private fun GooglePayConfiguration.toDeferredTransactionInfo(currency: String) =
    deferredParameters?.let { parameters ->
        GooglePayDeferredTransactionInfo(
            currencyCode = currency,
            countryCode = transactionCountryCode,
            transactionId = transactionId,
            managementUrl = parameters.managementUrl,
            billingAgreement = parameters.billingAgreement,
            immediateTotalPrice = parameters.immediateTotalPrice,
            immediateDisplayItems = parameters.immediateDisplayItems,
            billingDateTime = parameters.billingDateTime,
            priceStatus = parameters.priceStatus,
            price = parameters.price,
            label = parameters.label,
            displayItems = parameters.displayItems,
        )
    }

private fun GooglePayConfiguration.toTransactionInfo(
    price: String,
    currency: String,
) = GooglePayTransactionInfo(
    currencyCode = currency,
    countryCode = transactionCountryCode,
    transactionId = transactionId,
    totalPriceStatus = totalPriceStatus,
    totalPrice = price,
    totalPriceLabel = totalPriceLabel,
    checkoutOption = checkoutOption,
)

@Throws(IllegalArgumentException::class, JsonSyntaxException::class)
internal fun PaymentData.toGooglePayRequest(judo: Judo): GooglePayRequest {
    val gPayPaymentData = Gson().fromJson(toJson(), GooglePayPaymentData::class.java)

    val wallet =
        GooglePayWallet
            .Builder()
            .setGooglePayPaymentData(gPayPaymentData)
            .build()

    val amount = judo.amount
    val reference = judo.reference

    return GooglePayRequest
        .Builder()
        .setJudoId(judo.judoId)
        .setAmount(amount.amount)
        .setCurrency(amount.currency.name)
        .setYourPaymentReference(reference.paymentReference)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setCardAddress(judo.address)
        .setGooglePayWallet(wallet)
        .build()
}

@Throws(IllegalArgumentException::class, JsonSyntaxException::class)
internal fun PaymentData.toPreAuthGooglePayRequest(judo: Judo): PreAuthGooglePayRequest {
    val gPayPaymentData = Gson().fromJson(toJson(), GooglePayPaymentData::class.java)

    val wallet =
        GooglePayWallet
            .Builder()
            .setGooglePayPaymentData(gPayPaymentData)
            .build()

    val amount = judo.amount
    val reference = judo.reference

    return PreAuthGooglePayRequest
        .Builder()
        .setJudoId(judo.judoId)
        .setAmount(amount.amount)
        .setCurrency(amount.currency.name)
        .setYourPaymentReference(reference.paymentReference)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setCardAddress(judo.address)
        .setGooglePayWallet(wallet)
        .setDelayedAuthorisation(judo.delayedAuthorisation ?: false)
        .setAllowIncrement(judo.allowIncrement ?: false)
        .build()
}
