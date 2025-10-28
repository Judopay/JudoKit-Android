package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import com.judopay.judokit.android.model.PrimaryAccountDetails
import com.judopay.judokit.android.requireNotNullOrEmpty

@Suppress("LongParameterList")
class PreAuthTokenRequest private constructor(
    private var yourPaymentReference: String?,
    private var amount: String?,
    private var currency: String?,
    private var judoId: String?,
    private var yourConsumerReference: String?,
    private var yourPaymentMetaData: Map<String, String>?,
    private var endDate: String?,
    private var cardLastFour: String?,
    private var cardToken: String,
    private var cardType: Int,
    private var cv2: String?,
    private var cardAddress: Address?,
    private var emailAddress: String?,
    private var mobileNumber: String?,
    private var phoneCountryCode: String?,
    private var primaryAccountDetails: PrimaryAccountDetails?,
    private var initialRecurringPayment: Boolean?,
    private var threeDSecure: ThreeDSecureTwo?,
    private var cardHolderName: String?,
    private var delayedAuthorisation: Boolean = false,
    private var allowIncrement: Boolean = false,
    private var disableNetworkTokenisation: Boolean = false,
) {
    @Suppress("TooManyFunctions")
    class Builder {
        private var yourPaymentReference: String? = null
        private var amount: String? = null
        private var currency: String? = null
        private var judoId: String? = null
        private var yourConsumerReference: String? = null
        private var yourPaymentMetaData: Map<String, String>? = null
        private var endDate: String? = null
        private var cardLastFour: String? = null
        private var cardToken: String? = null
        private var cardType: Int = 0
        private var cv2: String? = null
        private var address: Address? = null
        private var emailAddress: String? = null
        private var mobileNumber: String? = null
        private var phoneCountryCode: String? = null
        private var primaryAccountDetails: PrimaryAccountDetails? = null
        private var initialRecurringPayment: Boolean? = null
        private var threeDSecure: ThreeDSecureTwo? = null
        private var cardHolderName: String? = null
        private var delayedAuthorisation: Boolean = false
        private var allowIncrement: Boolean = false
        private var disableNetworkTokenisation: Boolean = false

        fun setYourPaymentReference(yourPaymentReference: String?) = apply { this.yourPaymentReference = yourPaymentReference }

        fun setAmount(amount: String?) = apply { this.amount = amount }

        fun setCurrency(currency: String?) = apply { this.currency = currency }

        fun setJudoId(judoId: String?) = apply { this.judoId = judoId }

        fun setYourConsumerReference(yourConsumerReference: String?) = apply { this.yourConsumerReference = yourConsumerReference }

        fun setYourPaymentMetaData(yourPaymentMetaData: Map<String, String>?) = apply { this.yourPaymentMetaData = yourPaymentMetaData }

        fun setEndDate(endDate: String?) = apply { this.endDate = endDate }

        fun setCardLastFour(cardLastFour: String?) = apply { this.cardLastFour = cardLastFour }

        fun setCardToken(cardToken: String?) = apply { this.cardToken = cardToken }

        fun setCardType(cardType: Int) = apply { this.cardType = cardType }

        fun setCv2(cv2: String?) = apply { this.cv2 = cv2 }

        fun setAddress(address: Address?) = apply { this.address = address }

        fun setEmailAddress(emailAddress: String?) = apply { this.emailAddress = emailAddress }

        fun setMobileNumber(mobileNumber: String?) = apply { this.mobileNumber = mobileNumber }

        fun setPrimaryAccountDetails(primaryAccountDetails: PrimaryAccountDetails?) =
            apply { this.primaryAccountDetails = primaryAccountDetails }

        fun setInitialRecurringPayment(initialRecurringPayment: Boolean?) = apply { this.initialRecurringPayment = initialRecurringPayment }

        fun setThreeDSecure(threeDSecureTwo: ThreeDSecureTwo?) = apply { this.threeDSecure = threeDSecureTwo }

        fun setPhoneCountryCode(phoneCountryCode: String?) = apply { this.phoneCountryCode = phoneCountryCode }

        fun setCardHolderName(cardHolderName: String?) = apply { this.cardHolderName = cardHolderName }

        fun setDelayedAuthorisation(delayedAuthorisation: Boolean) = apply { this.delayedAuthorisation = delayedAuthorisation }

        fun setAllowIncrement(allowIncrement: Boolean) = apply { this.allowIncrement = allowIncrement }

        fun setDisableNetworkTokenisation(disabled: Boolean) = apply { this.disableNetworkTokenisation = disabled }

        fun build(): PreAuthTokenRequest {
            val id = requireNotNullOrEmpty(judoId, "judoId")
            val myAmount = requireNotNullOrEmpty(amount, "amount")
            val myCurrency = requireNotNullOrEmpty(currency, "currency")
            val consumerReference =
                requireNotNullOrEmpty(yourConsumerReference, "yourConsumerReference")
            val paymentReference =
                requireNotNullOrEmpty(yourPaymentReference, "yourPaymentReference")
            val myCardToken = requireNotNullOrEmpty(cardToken, "cardToken")
            val myThreeDSecure =
                com.judopay.judokit.android
                    .requireNotNull(threeDSecure, "threeDSecure")

            return PreAuthTokenRequest(
                paymentReference,
                myAmount,
                myCurrency,
                id,
                consumerReference,
                yourPaymentMetaData,
                endDate,
                cardLastFour,
                myCardToken,
                cardType,
                cv2,
                address,
                emailAddress,
                mobileNumber,
                phoneCountryCode,
                primaryAccountDetails,
                initialRecurringPayment,
                myThreeDSecure,
                cardHolderName,
                delayedAuthorisation,
                allowIncrement,
                disableNetworkTokenisation,
            )
        }
    }
}
