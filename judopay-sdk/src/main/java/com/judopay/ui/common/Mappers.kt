package com.judopay.ui.common

import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.judopay.Judo
import com.judopay.api.model.request.GooglePayRequest
import com.judopay.api.model.request.GooglePayWallet
import com.judopay.model.Currency
import com.judopay.model.GooglePayConfiguration
import com.judopay.model.googlepay.GPayPaymentGatewayParameters
import com.judopay.model.googlepay.GooglePayAuthMethod
import com.judopay.model.googlepay.GooglePayCardParameters
import com.judopay.model.googlepay.GooglePayIsReadyToPayRequest
import com.judopay.model.googlepay.GooglePayMerchantInfo
import com.judopay.model.googlepay.GooglePayPaymentData
import com.judopay.model.googlepay.GooglePayPaymentDataRequest
import com.judopay.model.googlepay.GooglePayPaymentMethod
import com.judopay.model.googlepay.GooglePayPaymentMethodTokenizationSpecification
import com.judopay.model.googlepay.GooglePayPaymentMethodType
import com.judopay.model.googlepay.GooglePayTokenizationSpecificationType
import com.judopay.model.googlepay.GooglePayTransactionInfo
import com.judopay.model.isSupportedByGooglePay
import com.judopay.toJSONString
import com.judopay.toMap

internal fun GooglePayConfiguration.toGooglePayPaymentMethod(judo: Judo): GooglePayPaymentMethod {
    val networks = judo.supportedCardNetworks.filter { it.isSupportedByGooglePay }

    val cardParameters = GooglePayCardParameters(
        allowedAuthMethods = arrayOf(
            GooglePayAuthMethod.CRYPTOGRAM_3DS
        ),
        allowedCardNetworks = networks.toTypedArray(),
        allowPrepaidCards = true,
        billingAddressRequired = isBillingAddressRequired,
        billingAddressParameters = billingAddressParameters
    )

    val tokenizationSpecification = GooglePayPaymentMethodTokenizationSpecification(
        type = GooglePayTokenizationSpecificationType.PAYMENT_GATEWAY,
        parameters = GPayPaymentGatewayParameters(gatewayMerchantId = judo.judoId)
    )

    return GooglePayPaymentMethod(
        type = GooglePayPaymentMethodType.CARD,
        parameters = cardParameters,
        tokenizationSpecification = tokenizationSpecification
    )
}

internal fun GooglePayConfiguration.toIsReadyToPayRequest(judo: Judo): IsReadyToPayRequest {
    val cardPaymentMethod = toGooglePayPaymentMethod(judo)

    val isReadyToPayRequest = GooglePayIsReadyToPayRequest(
        apiVersion = GOOGLE_PAY_API_VERSION,
        apiVersionMinor = GOOGLE_PAY_API_VERSION_MINOR,
        allowedPaymentMethods = arrayOf(cardPaymentMethod)
    )

    val json = isReadyToPayRequest.toJSONString()
    return IsReadyToPayRequest.fromJson(json)
}

internal fun GooglePayConfiguration.toPaymentDataRequest(judo: Judo): PaymentDataRequest {
    val price = judo.amount?.amount
    val currency = judo.amount?.currency?.name ?: Currency.GBP.name

    val transactionInfo = GooglePayTransactionInfo(
        currencyCode = currency,
        countryCode = transactionCountryCode,
        transactionId = transactionId,
        totalPriceStatus = totalPriceStatus,
        totalPrice = price,
        totalPriceLabel = totalPriceLabel,
        checkoutOption = checkoutOption
    )

    val cardPaymentMethod = toGooglePayPaymentMethod(judo)

    val paymentRequest = GooglePayPaymentDataRequest(
        apiVersion = GOOGLE_PAY_API_VERSION,
        apiVersionMinor = GOOGLE_PAY_API_VERSION_MINOR,
        merchantInfo = GooglePayMerchantInfo(merchantName),
        allowedPaymentMethods = arrayOf(cardPaymentMethod),
        transactionInfo = transactionInfo,
        emailRequired = isEmailRequired,
        shippingAddressRequired = isShippingAddressRequired,
        shippingAddressParameters = shippingAddressParameters
    )

    val json = paymentRequest.toJSONString()
    return PaymentDataRequest.fromJson(json)
}

@Throws(IllegalArgumentException::class, JsonSyntaxException::class)
internal fun PaymentData.toGooglePayRequest(judo: Judo): GooglePayRequest {
    val gPayPaymentData = Gson().fromJson(toJson(), GooglePayPaymentData::class.java)
    val info = gPayPaymentData.paymentMethodData.info
    val token = gPayPaymentData.paymentMethodData.tokenizationData.token

    val wallet = GooglePayWallet.Builder()
        .setCardNetwork(info.cardNetwork)
        .setCardDetails(info.cardDetails)
        .setToken(token)
        .build()

    val amount = judo.amount
    val reference = judo.reference

    return GooglePayRequest.Builder()
        .setJudoId(judo.judoId)
        .setAmount(amount?.amount)
        .setCurrency(amount?.currency?.name)
        .setYourPaymentReference(reference.paymentReference)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setGooglePayWallet(wallet)
        .build()
}
