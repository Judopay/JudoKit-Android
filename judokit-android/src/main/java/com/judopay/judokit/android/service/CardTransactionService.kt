package com.judopay.judokit.android.service

import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeStatusReceiver
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.request.CheckCardRequest
import com.judopay.judokit.android.api.model.request.PaymentRequest
import com.judopay.judokit.android.api.model.request.RegisterCardRequest
import com.judopay.judokit.android.api.model.request.SaveCardRequest
import com.judopay.judokit.android.api.model.request.TokenRequest
import com.judopay.judokit.android.api.model.request.threedsecure.EphemeralPublicKey
import com.judopay.judokit.android.api.model.request.threedsecure.SdkParameters
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecure
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.api.model.response.Consumer
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.toCardVerificationModel
import com.judopay.judokit.android.api.model.response.toChallengeParameters
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.displayName
import com.judopay.judokit.android.model.typeId
import com.judopay.judokit.android.toMap
import com.judopay.judokit.android.ui.cardentry.model.InputModel
import com.judopay.judokit.android.ui.cardverification.THREE_DS_ONE_DIALOG_FRAGMENT_TAG
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCardVerificationDialogFragment
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCompletionCallback
import retrofit2.await
import java.util.Date

interface CardTransactionCallback {
    fun onFinish(result: JudoPaymentResult)
}

class CardTransactionService(
    private val activity: FragmentActivity,
    private val judo: Judo,
    private val service: JudoApiService,
    private val transaction: Transaction
) : ThreeDSOneCompletionCallback, ChallengeStatusReceiver {

    lateinit var result: JudoPaymentResult
    lateinit var callback: CardTransactionCallback

    suspend fun makeTransaction(inputModel: InputModel, callback: CardTransactionCallback) {
        this.callback = callback
        val address = Address.Builder().apply {
            setLine1(judo.address?.line1)
            setLine2(judo.address?.line2)
            setLine3(judo.address?.line3)
            setTown(judo.address?.town)
            setBillingCountry(judo.address?.billingCountry)
            setPostCode(judo.address?.postCode)
            setCountryCode(judo.address?.countryCode)
            if (judo.uiConfiguration.avsEnabled) {
                setBillingCountry(inputModel.country)
                setPostCode(inputModel.postCode)
            }
        }.build()

        val apiResult = when (judo.paymentWidgetType) {
            PaymentWidgetType.CARD_PAYMENT -> performPaymentRequest(address, inputModel)
            PaymentWidgetType.PRE_AUTH -> performPreAuthPaymentRequest(address, inputModel)
            PaymentWidgetType.REGISTER_CARD -> performRegisterCardRequest(address, inputModel)
            PaymentWidgetType.CHECK_CARD -> performCheckCardRequest(address, inputModel)
            PaymentWidgetType.CREATE_CARD_TOKEN,
            PaymentWidgetType.PAYMENT_METHODS,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS -> performSaveCardRequest(
                address,
                inputModel
            )
            else -> throw IllegalStateException("Unsupported PaymentWidgetType")
        }
        handleApiResult(apiResult, callback)
    }

    internal suspend fun tokenPayment(
        card: TokenizedCardEntity,
        securityCode: String?,
        callback: CardTransactionCallback
    ) {
        this.callback = callback
        if (judo.paymentWidgetType == PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS) {
            buildReceipt(card)
        } else {
            val request = TokenRequest.Builder()
                .setAmount(judo.amount.amount)
                .setCurrency(judo.amount.currency.name)
                .setJudoId(judo.judoId)
                .setYourPaymentReference(judo.reference.paymentReference)
                .setYourConsumerReference(judo.reference.consumerReference)
                .setYourPaymentMetaData(judo.reference.metaData?.toMap())
                .setCardLastFour(card.ending)
                .setCardToken(card.token)
                .setCardType(card.network.typeId)
                .setCv2(securityCode)
                .setAddress(judo.address)
                .setInitialRecurringPayment(judo.initialRecurringPayment)
                .build()

            val response = when (judo.paymentWidgetType) {
                PaymentWidgetType.PAYMENT_METHODS -> service.tokenPayment(request).await()
                PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> service.preAuthTokenPayment(
                    request
                ).await()
                else -> throw IllegalStateException("Unexpected payment widget type: ${judo.paymentWidgetType}")
            }
            handleApiResult(response, callback)
        }
    }

    private fun handleApiResult(
        apiResult: JudoApiCallResult<Receipt>,
        callback: CardTransactionCallback
    ) {
        result = apiResult.toJudoPaymentResult(activity.resources)
        if (apiResult is JudoApiCallResult.Success) {
            val receipt = apiResult.data
            if (receipt != null) {
                when {
                    receipt.is3dSecureRequired -> {
                        ThreeDSOneCardVerificationDialogFragment(
                            service,
                            receipt.toCardVerificationModel(),
                            this
                        ).show(activity.supportFragmentManager, THREE_DS_ONE_DIALOG_FRAGMENT_TAG)
                    }
                    receipt.is3dSecure2Required -> {
                        transaction.doChallenge(
                            activity,
                            receipt.toChallengeParameters(),
                            this,
                            5
                        )
                    }
                    else -> callback.onFinish(result)
                }
            }
        } else if (apiResult is JudoApiCallResult.Failure) {
            callback.onFinish(result)
        }
    }

    private suspend fun performPaymentRequest(
        address: Address?,
        inputModel: InputModel
    ): JudoApiCallResult<Receipt> {
        val request = buildPaymentRequest(address, inputModel)
        return service.payment(request).await()
    }

    private suspend fun performPreAuthPaymentRequest(
        address: Address?,
        inputModel: InputModel
    ): JudoApiCallResult<Receipt> {
        val request = buildPaymentRequest(address, inputModel)
        return service.preAuthPayment(request).await()
    }

    private suspend fun performRegisterCardRequest(
        address: Address?,
        inputModel: InputModel
    ): JudoApiCallResult<Receipt> {
        val request = buildRegisterCardRequest(address, inputModel)
        return service.registerCard(request).await()
    }

    private suspend fun performCheckCardRequest(
        address: Address?,
        inputModel: InputModel
    ): JudoApiCallResult<Receipt> {
        val request = buildCheckCardRequest(address, inputModel)
        return service.checkCard(request).await()
    }

    private suspend fun performSaveCardRequest(
        address: Address?,
        inputModel: InputModel
    ): JudoApiCallResult<Receipt> {
        val request = buildSaveCardRequest(address, inputModel)
        return service.saveCard(request).await()
    }

    private fun buildPaymentRequest(address: Address?, inputModel: InputModel) =
        PaymentRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setAmount(judo.amount.amount)
            .setCurrency(judo.amount.currency.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(address)
            .setCardNumber(inputModel.cardNumber)
            .setCv2(inputModel.securityNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .setInitialRecurringPayment(judo.initialRecurringPayment)
            .setChallengeRequestIndicator(judo.challengeRequestIndicator)
            .setScaExemption(judo.scaExemption)
            .setCardHolderName(inputModel.cardHolderName)
            .setMobileNumber(judo.mobileNumber)
            .setEmailAddress(judo.emailAddress)
            .setPhoneCountryCode("44")
            .setThreeDSecure(buildThreeDSecureParameters())
            .build()

    private fun buildRegisterCardRequest(
        address: Address?,
        inputModel: InputModel
    ) = RegisterCardRequest.Builder()
        .setUniqueRequest(false)
        .setYourPaymentReference(judo.reference.paymentReference)
        .setCurrency(judo.amount.currency.name)
        .setJudoId(judo.judoId)
        .setYourConsumerReference(judo.reference.consumerReference)
        .setYourPaymentMetaData(judo.reference.metaData?.toMap())
        .setAddress(address)
        .setCardNumber(inputModel.cardNumber)
        .setExpiryDate(inputModel.expirationDate)
        .setCv2(inputModel.securityNumber)
        .setPrimaryAccountDetails(judo.primaryAccountDetails)
        .setAmount(judo.amount.amount)
        .setInitialRecurringPayment(judo.initialRecurringPayment)
        .setThreeDSecure(buildThreeDSecureParameters())
        .setCardHolderName(inputModel.cardHolderName)
        .setMobileNumber(judo.mobileNumber)
        .setEmailAddress(judo.emailAddress)
        .setPhoneCountryCode("44")
        .build()

    private fun buildSaveCardRequest(address: Address?, inputModel: InputModel) =
        SaveCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setCurrency(judo.amount.currency.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(address)
            .setCardNumber(inputModel.cardNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setCv2(inputModel.securityNumber)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .build()

    private fun buildCheckCardRequest(address: Address?, inputModel: InputModel) =
        CheckCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setCurrency(judo.amount.currency.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(address)
            .setCardNumber(inputModel.cardNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setCv2(inputModel.securityNumber)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .setInitialRecurringPayment(judo.initialRecurringPayment)
            .setThreeDSecure(buildThreeDSecureParameters())
            .setCardHolderName(inputModel.cardHolderName)
            .setMobileNumber(judo.mobileNumber)
            .setEmailAddress(judo.emailAddress)
            .setPhoneCountryCode("44")
            .build()

    private fun buildThreeDSecureParameters(): ThreeDSecure {
        val parameters = transaction.getAuthenticationRequestParameters()
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
                .setMaxTimeout(42)
                .setReferenceNumber(getSDKReferenceNumber())
                .setTransactionId(getSDKTransactionID())
                .build()
        }
        return ThreeDSecure.Builder()
            .setChallengeRequestIndicator(judo.challengeRequestIndicator)
            .setScaExemption(judo.scaExemption)
            .setSdkParameters(sdkParameters)
            .build()
    }

    private fun buildReceipt(card: TokenizedCardEntity) = with(card) {
        val receipt = Receipt(
            judoId = judo.judoId.toLong(),
            yourPaymentReference = judo.reference.paymentReference,
            createdAt = Date(),
            amount = judo.amount.amount.toBigDecimal(),
            currency = judo.amount.currency.name,
            consumer = Consumer(yourConsumerReference = judo.reference.consumerReference),
            cardDetails = CardToken(
                lastFour = ending,
                token = token,
                type = network.typeId,
                scheme = network.displayName
            )
        )
        callback.onFinish(JudoPaymentResult.Success(receipt.toJudoResult()))
    }

    override fun onSuccess(success: JudoPaymentResult) {
        callback.onFinish(success as JudoPaymentResult.Success)
    }

    override fun onFailure(error: JudoPaymentResult) {
        callback.onFinish(error as JudoPaymentResult.Error)
    }

    override fun cancelled() {
        TODO("Not yet implemented")
    }

    override fun completed(completionEvent: CompletionEvent) {
        TODO("Not yet implemented")
    }

    override fun protocolError(protocolErrorEvent: ProtocolErrorEvent) {
        TODO("Not yet implemented")
    }

    override fun runtimeError(runtimeErrorEvent: RuntimeErrorEvent) {
        TODO("Not yet implemented")
    }

    override fun timedout() {
        TODO("Not yet implemented")
    }
}
