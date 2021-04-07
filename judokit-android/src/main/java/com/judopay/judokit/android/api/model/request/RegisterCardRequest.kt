package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecure
import com.judopay.judokit.android.model.PrimaryAccountDetails
import com.judopay.judokit.android.requireNotNull
import com.judopay.judokit.android.requireNotNullOrEmpty

private const val DEFAULT_AMOUNT = "0.01"

class RegisterCardRequest private constructor(
    private var uniqueRequest: Boolean?,
    private var yourPaymentReference: String,
    private var currency: String,
    private var judoId: String,
    private var yourConsumerReference: String,
    private var yourPaymentMetaData: Map<String, String>?,
    private var cardAddress: Address,
    private var cardNumber: String,
    private var cv2: String,
    private var expiryDate: String,
    private var startDate: String?,
    private var issueNumber: String?,
    private var emailAddress: String?,
    private var mobileNumber: String?,
    private var phoneCountryCode: String?,
    private var primaryAccountDetails: PrimaryAccountDetails?,
    private var amount: String,
    private var initialRecurringPayment: Boolean?,
    private var threeDSecure: ThreeDSecure?,
    private var cardHolderName: String?
) {
    class Builder {
        private var uniqueRequest: Boolean? = null
        private var yourPaymentReference: String? = null
        private var currency: String? = null
        private var judoId: String? = null
        private var yourConsumerReference: String? = null
        private var yourPaymentMetaData: Map<String, String>? = null
        private var address: Address? = null
        private var cardNumber: String? = null
        private var cv2: String? = null
        private var expiryDate: String? = null
        private var startDate: String? = null
        private var issueNumber: String? = null
        private var emailAddress: String? = null
        private var mobileNumber: String? = null
        private var primaryAccountDetails: PrimaryAccountDetails? = null
        private var amount: String? = null
        private var initialRecurringPayment: Boolean? = null
        private var threeDSecure: ThreeDSecure? = null
        private var cardHolderName: String? = null
        private var phoneCountryCode: String? = null

        fun setUniqueRequest(uniqueRequest: Boolean?) = apply { this.uniqueRequest = uniqueRequest }

        fun setYourPaymentReference(yourPaymentReference: String?) =
            apply { this.yourPaymentReference = yourPaymentReference }

        fun setCurrency(currency: String?) = apply { this.currency = currency }

        fun setJudoId(judoId: String?) = apply { this.judoId = judoId }

        fun setYourConsumerReference(yourConsumerReference: String?) =
            apply { this.yourConsumerReference = yourConsumerReference }

        fun setYourPaymentMetaData(yourPaymentMetaData: Map<String, String>?) =
            apply { this.yourPaymentMetaData = yourPaymentMetaData }

        fun setAddress(address: Address?) = apply { this.address = address }

        fun setCardNumber(cardNumber: String?) = apply { this.cardNumber = cardNumber }

        fun setCv2(cv2: String?) = apply { this.cv2 = cv2 }

        fun setExpiryDate(expiryDate: String?) = apply { this.expiryDate = expiryDate }

        fun setStartDate(startDate: String?) = apply { this.startDate = startDate }

        fun setIssueNumber(issueNumber: String?) = apply { this.issueNumber = issueNumber }

        fun setEmailAddress(emailAddress: String?) = apply { this.emailAddress = emailAddress }

        fun setMobileNumber(mobileNumber: String?) = apply { this.mobileNumber = mobileNumber }

        fun setPrimaryAccountDetails(primaryAccountDetails: PrimaryAccountDetails?) =
            apply { this.primaryAccountDetails = primaryAccountDetails }

        fun setAmount(amount: String?) =
            apply { this.amount = amount }

        fun setInitialRecurringPayment(initialRecurringPayment: Boolean?) =
            apply { this.initialRecurringPayment = initialRecurringPayment }

        fun setThreeDSecure(threeDSecure: ThreeDSecure?) = apply { this.threeDSecure = threeDSecure }

        fun setCardHolderName(cardHolderName: String?) =
            apply { this.cardHolderName = cardHolderName }

        fun setPhoneCountryCode(phoneCountryCode: String?) =
            apply { this.phoneCountryCode = phoneCountryCode }

        fun build(): RegisterCardRequest {
            val id = requireNotNullOrEmpty(judoId, "judoId")
            val myCurrency = requireNotNullOrEmpty(currency, "currency")
            val consumerReference =
                requireNotNullOrEmpty(yourConsumerReference, "yourConsumerReference")
            val myCardNumber = requireNotNullOrEmpty(cardNumber, "cardNumber")
            val myCv2 = requireNotNullOrEmpty(cv2, "cv2")
            val myExpiryDate = requireNotNullOrEmpty(expiryDate, "expiryDate")
            val paymentReference =
                requireNotNullOrEmpty(yourPaymentReference, "yourPaymentReference")
            val myAddress = requireNotNull(address, "address")
            val myAmount = if (amount.isNullOrEmpty()) DEFAULT_AMOUNT else amount!!
            val myThreeDSecure = requireNotNull(threeDSecure)

            return RegisterCardRequest(
                uniqueRequest,
                paymentReference,
                myCurrency,
                id,
                consumerReference,
                yourPaymentMetaData,
                myAddress,
                myCardNumber,
                myCv2,
                myExpiryDate,
                startDate,
                issueNumber,
                emailAddress,
                mobileNumber,
                phoneCountryCode,
                primaryAccountDetails,
                myAmount,
                initialRecurringPayment,
                myThreeDSecure,
                cardHolderName
            )
        }
    }
}
