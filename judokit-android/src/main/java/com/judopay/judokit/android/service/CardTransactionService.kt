package com.judopay.judokit.android.service

import android.util.Base64
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ConfigParameters
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.service.ThreeDS2ServiceImpl
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import com.judopay.judo3ds2.transaction.challenge.ChallengeStatusReceiver
import com.judopay.judo3ds2.ui.views.ProgressDialog
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
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import com.judopay.judokit.android.api.model.response.CReqParameters
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.api.model.response.Consumer
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.toCardVerificationModel
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.TransactionDetail
import com.judopay.judokit.android.model.displayName
import com.judopay.judokit.android.model.isPaymentMethodsWidget
import com.judopay.judokit.android.model.typeId
import com.judopay.judokit.android.toMap
import com.judopay.judokit.android.ui.cardverification.THREE_DS_ONE_DIALOG_FRAGMENT_TAG
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCardVerificationDialogFragment
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCompletionCallback
import com.judopay.judokit.android.ui.common.getLocale
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.Date

private const val THREE_DS_TWO_MIN_TIMEOUT = 5

interface CardTransactionCallback {
    fun onFinish(result: JudoPaymentResult)
}

class CardTransactionService(
    private val activity: FragmentActivity,
    private val judo: Judo,
    private val service: JudoApiService
) {

    private val challengeStatusReceiver = object : ChallengeStatusReceiver {
        override fun cancelled() {
            progressDialog?.dismiss()
            TODO("Not yet implemented")
        }

        override fun completed(completionEvent: CompletionEvent) {
            progressDialog?.dismiss()
            TODO("Not yet implemented")
        }

        override fun protocolError(protocolErrorEvent: ProtocolErrorEvent) {
            progressDialog?.dismiss()
            TODO("Not yet implemented")
        }

        override fun runtimeError(runtimeErrorEvent: RuntimeErrorEvent) {
            progressDialog?.dismiss()
            TODO("Not yet implemented")
        }

        override fun timedout() {
            progressDialog?.dismiss()
            TODO("Not yet implemented")
        }
    }

   private val threeDSOneCompletionCallback = object : ThreeDSOneCompletionCallback{
       override fun onSuccess(success: JudoPaymentResult) {
           transaction.close()
           callback.onFinish(success as JudoPaymentResult.Success)
       }

       override fun onFailure(error: JudoPaymentResult) {
           transaction.close()
           callback.onFinish(error)
       }
   }
    private var threeDS2Service: ThreeDS2ServiceImpl = ThreeDS2ServiceImpl()
    private lateinit var result: JudoPaymentResult
    private lateinit var callback: CardTransactionCallback
    private lateinit var transaction: Transaction
    private var progressDialog: ProgressDialog? = null

    init {
        threeDS2Service.initialize(
            activity,
            ConfigParameters(),
            getLocale(activity.resources),
            null
        )
    }

    fun makeTransaction(transactionDetail: TransactionDetail, callback: CardTransactionCallback) {
        try {
            this.callback = callback
            activity.lifecycleScope.launch {
                transaction = threeDS2Service.createTransaction(
                    "F000000000",
                    "2.1.0"
                )
                val address =
                    if (judo.is3DS2Enabled && !judo.paymentWidgetType.isPaymentMethodsWidget) {
                        Address.Builder().apply {
                            setLine1(transactionDetail.addressLine1)
                            setLine2(transactionDetail.addressLine2)
                            setLine3(transactionDetail.addressLine3)
                            setTown(transactionDetail.city)
                            setPostCode(transactionDetail.postalCode)
                            setCountryCode(transactionDetail.country?.toIntOrNull())
                        }.build()
                    } else Address.Builder().build()

                val apiResult = when (judo.paymentWidgetType) {
                    PaymentWidgetType.CARD_PAYMENT -> performPaymentRequest(
                        address,
                        transactionDetail
                    )
                    PaymentWidgetType.PRE_AUTH -> performPreAuthPaymentRequest(
                        address,
                        transactionDetail
                    )
                    PaymentWidgetType.REGISTER_CARD -> performRegisterCardRequest(
                        address,
                        transactionDetail
                    )
                    PaymentWidgetType.CHECK_CARD -> performCheckCardRequest(
                        address,
                        transactionDetail
                    )
                    PaymentWidgetType.CREATE_CARD_TOKEN,
                    PaymentWidgetType.PAYMENT_METHODS,
                    PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
                    PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS -> performSaveCardRequest(
                        address,
                        transactionDetail
                    )
                    else -> throw IllegalStateException("Unsupported PaymentWidgetType")
                }
                handleApiResult(apiResult, callback)
            }
        } catch (e: Exception) {
            callback.onFinish(JudoPaymentResult.Error(JudoError.judoInternalError(e.message)))
            progressDialog?.dismiss()
        }
    }

    fun tokenPayment(
        transactionDetail: TransactionDetail,
        callback: CardTransactionCallback
    ) {
        try {
            this.callback = callback
            activity.lifecycleScope.launch {
                transaction = threeDS2Service.createTransaction(
                    "F000000000",
                    "2.2.0"
                )
                val address =
                    if (judo.is3DS2Enabled) {
                        Address.Builder().apply {
                            setLine1(transactionDetail.addressLine1)
                            setLine2(transactionDetail.addressLine2)
                            setLine3(transactionDetail.addressLine3)
                            setTown(transactionDetail.city)
                            setPostCode(transactionDetail.postalCode)
                            setCountryCode(transactionDetail.country?.toIntOrNull())
                        }.build()
                    } else Address.Builder().build()
                if (judo.paymentWidgetType == PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS) {
                    buildReceipt(transactionDetail)
                } else {
                    val request = TokenRequest.Builder()
                        .setAmount(judo.amount.amount)
                        .setCurrency(judo.amount.currency.name)
                        .setJudoId(judo.judoId)
                        .setYourPaymentReference(judo.reference.paymentReference)
                        .setYourConsumerReference(judo.reference.consumerReference)
                        .setYourPaymentMetaData(judo.reference.metaData?.toMap())
                        .setCardLastFour(transactionDetail.cardLastFour)
                        .setCardToken(transactionDetail.cardToken)
                        .setCardType(transactionDetail.cardType?.typeId ?: 0)
                        .setCv2(transactionDetail.securityNumber)
                        .setInitialRecurringPayment(judo.initialRecurringPayment)
                        .setThreeDSecure(buildThreeDSecureParameters())
                        .setAddress(address)
                        .setMobileNumber(transactionDetail.mobileNumber)
                        .setEmailAddress(transactionDetail.email)
                        .setPhoneCountryCode(transactionDetail.phoneCountryCode)
                        .build()

                    val response = when (judo.paymentWidgetType) {
                        PaymentWidgetType.PAYMENT_METHODS, PaymentWidgetType.CARD_PAYMENT -> service.tokenPayment(
                            request
                        ).await()
                        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS, PaymentWidgetType.PRE_AUTH -> service.preAuthTokenPayment(
                            request
                        ).await()
                        else -> throw IllegalStateException("Unexpected payment widget type: ${judo.paymentWidgetType}")
                    }
                    handleApiResult(response, callback)
                }
            }
        } catch (e: Exception) {
            callback.onFinish(JudoPaymentResult.Error(JudoError.judoInternalError(e.message)))
            progressDialog?.dismiss()
        }
    }

    fun destroy() {
        threeDS2Service.cleanup(activity)
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
                            threeDSOneCompletionCallback
                        ).show(activity.supportFragmentManager, THREE_DS_ONE_DIALOG_FRAGMENT_TAG)
                    }
                    receipt.is3dSecure2Required -> {
                        val cReqParams = Gson().fromJson(
                            String(Base64.decode(receipt.cReq, Base64.NO_WRAP)),
                            CReqParameters::class.java
                        )
                        val challengeParameters = ChallengeParameters(
                            cReqParams.threeDSServerTransID,
                            cReqParams.acsTransID,
                            receipt.acsReferenceNumber,
                            receipt.acsSignedContent,
                            null
                        )
                        progressDialog = transaction.getProgressView(activity).also { it.show() }
                        transaction.doChallenge(
                            activity,
                            challengeParameters,
                            challengeStatusReceiver,
                            THREE_DS_TWO_MIN_TIMEOUT
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
        inputModel: TransactionDetail
    ): JudoApiCallResult<Receipt> {
        val request = buildPaymentRequest(address, inputModel)
        return service.payment(request).await()
    }

    private suspend fun performPreAuthPaymentRequest(
        address: Address?,
        inputModel: TransactionDetail
    ): JudoApiCallResult<Receipt> {
        val request = buildPaymentRequest(address, inputModel)
        return service.preAuthPayment(request).await()
    }

    private suspend fun performRegisterCardRequest(
        address: Address?,
        inputModel: TransactionDetail
    ): JudoApiCallResult<Receipt> {
        val request = buildRegisterCardRequest(address, inputModel)
        return service.registerCard(request).await()
    }

    private suspend fun performCheckCardRequest(
        address: Address?,
        inputModel: TransactionDetail
    ): JudoApiCallResult<Receipt> {
        val request = buildCheckCardRequest(address, inputModel)
        return service.checkCard(request).await()
    }

    private suspend fun performSaveCardRequest(
        address: Address?,
        inputModel: TransactionDetail
    ): JudoApiCallResult<Receipt> {
        val request = buildSaveCardRequest(address, inputModel)
        return service.saveCard(request).await()
    }

    private fun buildPaymentRequest(address: Address?, inputModel: TransactionDetail) =
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
            .setCardHolderName(inputModel.cardHolderName)
            .setMobileNumber(inputModel.mobileNumber)
            .setEmailAddress(inputModel.email)
            .setPhoneCountryCode(inputModel.phoneCountryCode)
            .setThreeDSecure(buildThreeDSecureParameters())
            .build()

    private fun buildRegisterCardRequest(
        address: Address?,
        inputModel: TransactionDetail
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
        .setMobileNumber(inputModel.mobileNumber)
        .setEmailAddress(inputModel.email)
        .setPhoneCountryCode(inputModel.phoneCountryCode)
        .build()

    private fun buildSaveCardRequest(address: Address?, inputModel: TransactionDetail) =
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

    private fun buildCheckCardRequest(address: Address?, inputModel: TransactionDetail) =
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
            .setMobileNumber(inputModel.mobileNumber)
            .setEmailAddress(inputModel.email)
            .setPhoneCountryCode(inputModel.phoneCountryCode)
            .build()

    private fun buildThreeDSecureParameters(): ThreeDSecureTwo {
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
                .setMaxTimeout(judo.threeDSTwoMaxTimeout)
                .setReferenceNumber(getSDKReferenceNumber())
                .setTransactionId(getSDKTransactionID())
                .build()
        }
        return ThreeDSecureTwo.Builder()
            .setChallengeRequestIndicator(judo.challengeRequestIndicator)
            .setScaExemption(judo.scaExemption)
            .setSdkParameters(sdkParameters)
            .build()
    }

    private fun buildReceipt(transactionDetail: TransactionDetail) = with(transactionDetail) {
        val receipt = Receipt(
            judoId = judo.judoId.toLong(),
            yourPaymentReference = judo.reference.paymentReference,
            createdAt = Date(),
            amount = judo.amount.amount.toBigDecimal(),
            currency = judo.amount.currency.name,
            consumer = Consumer(yourConsumerReference = judo.reference.consumerReference),
            cardDetails = CardToken(
                lastFour = cardLastFour,
                token = cardToken,
                type = cardType?.typeId ?: -1,
                scheme = cardType?.displayName
            )
        )
        callback.onFinish(JudoPaymentResult.Success(receipt.toJudoResult()))
    }
}
