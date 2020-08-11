package com.judokit.android.api

import com.judokit.android.Judo
import com.judokit.android.api.model.request.Address
import com.judokit.android.api.model.request.BankSaleRequest
import com.judokit.android.api.model.request.CheckCardRequest
import com.judokit.android.api.model.request.GooglePayRequest
import com.judokit.android.api.model.request.GooglePayWallet
import com.judokit.android.api.model.request.IdealSaleRequest
import com.judokit.android.api.model.request.PaymentRequest
import com.judokit.android.api.model.request.RegisterCardRequest
import com.judokit.android.api.model.request.SaveCardRequest
import com.judokit.android.api.model.request.TokenRequest
import com.judokit.android.toMap
import java.math.BigDecimal

fun createPaymentRequest(
    judo: Judo,
    cardNumber: String,
    expiryDate: String,
    securityCode: String
) = with(judo) {
    PaymentRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(reference.paymentReference)
        .setAmount(amount.amount)
        .setCurrency(amount.currency.name)
        .setJudoId(judoId)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setAddress(address)
        .setCardNumber(cardNumber)
        .setCv2(securityCode)
        .setExpiryDate(expiryDate)
        .setPrimaryAccountDetails(primaryAccountDetails)
        .build()
}

fun createRegisterCardRequest(
    judo: Judo,
    address: Address,
    cardNumber: String,
    expirationDate: String,
    securityCode: String
) = with(judo) {
    RegisterCardRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(reference.paymentReference)
        .setCurrency(amount.currency.name)
        .setJudoId(judoId)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setAddress(address)
        .setCardNumber(cardNumber)
        .setExpiryDate(expirationDate)
        .setCv2(securityCode)
        .setPrimaryAccountDetails(primaryAccountDetails)
        .setAmount(amount.amount)
        .build()
}

fun createSaveCardRequest(
    judo: Judo,
    address: Address,
    cardNumber: String,
    expirationDate: String,
    securityCode: String
) = with(judo) {
    SaveCardRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(reference.paymentReference)
        .setCurrency(amount.currency.name)
        .setJudoId(judoId)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setAddress(address)
        .setCardNumber(cardNumber)
        .setExpiryDate(expirationDate)
        .setCv2(securityCode)
        .setPrimaryAccountDetails(primaryAccountDetails)
        .build()
}

fun createCheckCardRequest(
    judo: Judo,
    address: Address,
    cardNumber: String,
    expirationDate: String,
    securityCode: String
) = with(judo) {
    CheckCardRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(reference.paymentReference)
        .setCurrency(amount.currency.name)
        .setJudoId(judoId)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setAddress(address)
        .setCardNumber(cardNumber)
        .setExpiryDate(expirationDate)
        .setCv2(securityCode)
        .setPrimaryAccountDetails(primaryAccountDetails)
        .build()
}

fun createGooglePayRequest(judo: Judo, cardNetwork: String, cardDetails: String, token: String) {
    val wallet = GooglePayWallet.Builder()
        .setCardNetwork(cardNetwork)
        .setCardDetails(cardDetails)
        .setToken(token)
        .build()

    return with(judo) {
        GooglePayRequest.Builder()
            .setJudoId(judoId)
            .setAmount(amount.amount)
            .setCurrency(amount.currency.name)
            .setYourPaymentReference(reference.paymentReference)
            .setYourConsumerReference(reference.consumerReference)
            .setYourPaymentMetaData(reference.metaData?.toMap())
            .setPrimaryAccountDetails(primaryAccountDetails)
            .setGooglePayWallet(wallet)
            .build()
    }
}

fun createIdealSaleRequest(judo: Judo, bic: String) = with(judo) {
    IdealSaleRequest.Builder()
        .setAmount(BigDecimal(amount.amount))
        .setMerchantConsumerReference(reference.consumerReference)
        .setMerchantPaymentReference(reference.paymentReference)
        .setPaymentMetadata(reference.metaData?.toMap())
        .setJudoId(judoId)
        .setBic(bic)
        .build()
}

fun createBankSaleRequest(judo: Judo) = with(judo) {
    BankSaleRequest.Builder()
        .setAmount(amount.amount.toBigDecimalOrNull())
        .setMerchantPaymentReference(reference.paymentReference)
        .setMerchantConsumerReference(reference.consumerReference)
        .setJudoId(judoId)
        .setMobileNumber(pbbaConfiguration?.mobileNumber)
        .setEmailAddress(pbbaConfiguration?.emailAddress)
        .setAppearsOnStatement(pbbaConfiguration?.appearsOnStatement)
        .setPaymentMetadata(reference.metaData?.toMap())
        .setMerchantRedirectUrl(pbbaConfiguration?.deepLinkScheme)
        .build()
}

fun createTokenRequest(judo: Judo, cardToken: String, securityCode: String? = null) = with(judo) {
    TokenRequest.Builder()
        .setAmount(amount.amount)
        .setCurrency(amount.currency.name)
        .setJudoId(judoId)
        .setYourPaymentReference(reference.paymentReference)
        .setYourConsumerReference(reference.consumerReference)
        .setYourPaymentMetaData(reference.metaData?.toMap())
        .setCardToken(cardToken)
        .setCv2(securityCode)
        .setPrimaryAccountDetails(primaryAccountDetails)
        .setAddress(Address.Builder().build())
        .build()
}
