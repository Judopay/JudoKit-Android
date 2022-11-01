package com.judopay.judokit.android.model

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.judopay.judo3ds2.exception.SDKRuntimeException
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.request.CheckCardRequest
import com.judopay.judokit.android.api.model.request.PaymentRequest
import com.judopay.judokit.android.api.model.request.RegisterCardRequest
import com.judopay.judokit.android.api.model.request.SaveCardRequest
import com.judopay.judokit.android.api.model.request.TokenRequest
import com.judopay.judokit.android.api.model.request.threedsecure.DeviceRenderOptions
import com.judopay.judokit.android.api.model.request.threedsecure.EphemeralPublicKey
import com.judopay.judokit.android.api.model.request.threedsecure.SdkParameters
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.api.model.response.Consumer
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.toMap
import java.util.Date

class TransactionDetails private constructor(
    val cardNumber: String?,
    val cardHolderName: String?,
    val expirationDate: String?,
    val securityNumber: String?,
    val country: String?,
    val email: String?,
    val phoneCountryCode: String?,
    val mobileNumber: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val addressLine3: String?,
    val city: String?,
    val postalCode: String?,
    val cardToken: String?,
    val cardType: CardNetwork?,
    val cardLastFour: String?,
    val state: String?
) {
    class Builder {
        private var cardNumber: String? = null
        private var cardHolderName: String? = null
        private var expirationDate: String? = null
        private var securityNumber: String? = null
        private var countryCode: String? = null
        private var state: String? = null
        private var email: String? = null
        private var phoneCountryCode: String? = null
        private var mobileNumber: String? = null
        private var addressLine1: String? = null
        private var addressLine2: String? = null
        private var addressLine3: String? = null
        private var city: String? = null
        private var postalCode: String? = null
        private var cardToken: String? = null
        private var cardType: CardNetwork? = null
        private var cardLastFour: String? = null

        fun setCardNumber(cardNumber: String?) = apply { this.cardNumber = cardNumber }
        fun setCardHolderName(cardHolderName: String?) = apply { this.cardHolderName = cardHolderName }

        fun setExpirationDate(expirationDate: String?) = apply { this.expirationDate = expirationDate }

        fun setSecurityNumber(securityNumber: String?) = apply { this.securityNumber = securityNumber }

        fun setCountryCode(countryCode: String?) = apply { this.countryCode = countryCode }
        fun setEmail(email: String?) = apply { this.email = if (email.isNullOrBlank()) null else email }
        fun setPhoneCountryCode(phoneCountryCode: String?) =
            apply { this.phoneCountryCode = if (phoneCountryCode.isNullOrBlank()) null else phoneCountryCode }

        fun setMobileNumber(mobileNumber: String?) =
            apply { this.mobileNumber = if (mobileNumber.isNullOrBlank()) null else mobileNumber }

        fun setAddressLine1(addressLine1: String?) = apply { this.addressLine1 = addressLine1 }
        fun setAddressLine2(addressLine2: String?) = apply { this.addressLine2 = addressLine2 }
        fun setAddressLine3(addressLine3: String?) = apply { this.addressLine3 = addressLine3 }
        fun setCity(city: String?) = apply { this.city = city }
        fun setPostalCode(postalCode: String?) = apply { this.postalCode = postalCode }
        fun setState(state: String?) = apply { this.state = state }
        fun setCardToken(cardToken: String?) = apply { this.cardToken = cardToken }
        fun setCardType(cardType: CardNetwork?) = apply { this.cardType = cardType }
        fun setCardLastFour(cardLastFour: String?) = apply { this.cardLastFour = cardLastFour }

        fun build(): TransactionDetails {
            if (cardType == null) {
                cardType = CardNetwork.ofNumber(cardNumber ?: "")
            }

            return TransactionDetails(
                cardNumber = cardNumber,
                cardHolderName = cardHolderName,
                expirationDate = expirationDate,
                securityNumber = securityNumber,
                country = countryCode,
                email = email,
                phoneCountryCode = phoneCountryCode,
                mobileNumber = mobileNumber,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                addressLine3 = addressLine3,
                city = city,
                postalCode = postalCode,
                cardToken = cardToken,
                cardType = cardType,
                cardLastFour = cardLastFour,
                state = state
            )
        }
    }
}

fun TransactionDetails.getAddress(judo: Judo): Address? {
    if (judo.uiConfiguration.shouldAskForBillingInformation) {
        return Address.Builder()
            .setLine1(addressLine1)
            .setLine2(addressLine2)
            .setLine3(addressLine3)
            .setTown(city)
            .setPostCode(postalCode)
            .setCountryCode(country?.toIntOrNull())
            .setState(state)
            .build()
    }

    return judo.address
}

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
fun Transaction.toThreeDSecureTwo(judo: Judo): ThreeDSecureTwo {
    val parameters = getAuthenticationRequestParameters()
    val sdkParameters = with(parameters) {
        SdkParameters.Builder()
            .setApplicationId(getSDKAppID())
            .setEncodedData(getDeviceData())
            .setEphemeralPublicKey(
                Gson().fromJson(
                    getSDKEphemeralPublicKey(),
                    EphemeralPublicKey::class.java
                )
            )
            .setMaxTimeout(judo.threeDSTwoMaxTimeout)
            .setReferenceNumber(getSDKReferenceNumber())
            .setTransactionId(getSDKTransactionID())
            .setDeviceRenderOptions(DeviceRenderOptions())
            .build()
    }
    return ThreeDSecureTwo.Builder()
        .setChallengeRequestIndicator(judo.challengeRequestIndicator)
        .setScaExemption(judo.scaExemption)
        .setSdkParameters(sdkParameters)
        .build()
}

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
fun TransactionDetails.toPaymentRequest(judo: Judo, transaction: Transaction): PaymentRequest {
    val myAmount = judo.amount
    val myReference = judo.reference

    return PaymentRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(myReference.paymentReference)
        .setAmount(myAmount.amount)
        .setCurrency(myAmount.currency.name)
        .setJudoId(judo.judoId)
        .setYourConsumerReference(myReference.consumerReference)
        .setYourPaymentMetaData(myReference.metaData?.toMap())
        .setAddress(getAddress(judo))
        .setCardNumber(cardNumber)
        .setCv2(securityNumber)
        .setExpiryDate(expirationDate)
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setInitialRecurringPayment(judo.initialRecurringPayment)
        .setCardHolderName(cardHolderName)
        .setMobileNumber(mobileNumber)
        .setEmailAddress(email)
        .setPhoneCountryCode(phoneCountryCode)
        .setThreeDSecure(transaction.toThreeDSecureTwo(judo))
        .build()
}

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
fun TransactionDetails.toCheckCardRequest(judo: Judo, transaction: Transaction): CheckCardRequest {
    val myAmount = judo.amount
    val myReference = judo.reference

    return CheckCardRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(myReference.paymentReference)
        .setCurrency(myAmount.currency.name)
        .setJudoId(judo.judoId)
        .setYourConsumerReference(myReference.consumerReference)
        .setYourPaymentMetaData(myReference.metaData?.toMap())
        .setAddress(getAddress(judo))
        .setCardNumber(cardNumber)
        .setExpiryDate(expirationDate)
        .setCv2(securityNumber)
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setInitialRecurringPayment(judo.initialRecurringPayment)
        .setThreeDSecure(transaction.toThreeDSecureTwo(judo))
        .setCardHolderName(cardHolderName)
        .setMobileNumber(mobileNumber)
        .setEmailAddress(email)
        .setPhoneCountryCode(phoneCountryCode)
        .build()
}

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
fun TransactionDetails.toSaveCardRequest(judo: Judo, transaction: Transaction): SaveCardRequest {
    return SaveCardRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(judo.reference.paymentReference)
        .setCurrency(judo.amount.currency.name)
        .setJudoId(judo.judoId)
        .setYourConsumerReference(judo.reference.consumerReference)
        .setYourPaymentMetaData(judo.reference.metaData?.toMap())
        .setCardNumber(cardNumber)
        .setExpiryDate(expirationDate)
        .setCv2(securityNumber)
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setAddress(judo.address)
        .build()
}

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
fun TransactionDetails.toRegisterCardRequest(judo: Judo, transaction: Transaction): RegisterCardRequest {
    val myAmount = judo.amount
    val myReference = judo.reference

    return RegisterCardRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(myReference.paymentReference)
        .setCurrency(myAmount.currency.name)
        .setAmount(myAmount.amount)
        .setJudoId(judo.judoId)
        .setYourConsumerReference(myReference.consumerReference)
        .setYourPaymentMetaData(myReference.metaData?.toMap())
        .setAddress(getAddress(judo))
        .setCardNumber(cardNumber)
        .setExpiryDate(expirationDate)
        .setCv2(securityNumber)
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setInitialRecurringPayment(judo.initialRecurringPayment)
        .setThreeDSecure(transaction.toThreeDSecureTwo(judo))
        .setCardHolderName(cardHolderName)
        .setMobileNumber(mobileNumber)
        .setEmailAddress(email)
        .setPhoneCountryCode(phoneCountryCode)
        .build()
}

@Throws(JsonSyntaxException::class, SDKRuntimeException::class, IllegalArgumentException::class)
fun TransactionDetails.toTokenRequest(judo: Judo, transaction: Transaction): TokenRequest {
    val myAmount = judo.amount
    val myReference = judo.reference

    return TokenRequest.Builder()
        .setJudoId(judo.judoId)
        .setAmount(myAmount.amount)
        .setCurrency(myAmount.currency.name)
        .setYourPaymentReference(myReference.paymentReference)
        .setYourConsumerReference(myReference.consumerReference)
        .setYourPaymentMetaData(myReference.metaData?.toMap())
        .setCardLastFour(cardLastFour)
        .setCardToken(cardToken)
        .setCardHolderName(cardHolderName)
        .setCardType(cardType?.typeId ?: 0)
        .setCv2(securityNumber)
        .setInitialRecurringPayment(judo.initialRecurringPayment)
        .setThreeDSecure(transaction.toThreeDSecureTwo(judo))
        .setMobileNumber(mobileNumber)
        .setEmailAddress(email)
        .setPhoneCountryCode(phoneCountryCode)
        .setAddress(getAddress(judo))
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .build()
}

fun TransactionDetails.toReceipt(judo: Judo): Receipt {
    val myAmount = judo.amount
    val myReference = judo.reference
    val myJudoId = judo.judoId
        .toCharArray()
        .filter { it.isDigit() }
        .joinToString(separator = "")
        .toLongOrNull()

    return Receipt(
        judoId = myJudoId,
        yourPaymentReference = myReference.paymentReference,
        createdAt = Date(),
        amount = myAmount.amount.toBigDecimal(),
        currency = myAmount.currency.name,
        consumer = Consumer(yourConsumerReference = myReference.consumerReference),
        cardDetails = CardToken(
            lastFour = cardLastFour,
            token = cardToken,
            type = cardType?.typeId ?: -1,
            scheme = cardType?.displayName
        )
    )
}
