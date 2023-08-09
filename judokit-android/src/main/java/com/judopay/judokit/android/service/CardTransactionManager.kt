package com.judopay.judokit.android.service

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.judopay.judo3ds2.exception.SDKAlreadyInitializedException
import com.judopay.judo3ds2.exception.SDKNotInitializedException
import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ConfigParameters
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.service.ThreeDS2Service
import com.judopay.judo3ds2.service.ThreeDS2ServiceImpl
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeStatusReceiver
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.RavelinApiService
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.request.Complete3DS2Request
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.RavelinEncryptionResponse
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.getCReqParameters
import com.judopay.judokit.android.api.model.response.getChallengeParameters
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.toCheckCardRequest
import com.judopay.judokit.android.model.toEncryptCardRequest
import com.judopay.judokit.android.model.toPaymentRequest
import com.judopay.judokit.android.model.toPreAuthRequest
import com.judopay.judokit.android.model.toPreAuthTokenRequest
import com.judopay.judokit.android.model.toRegisterCardRequest
import com.judopay.judokit.android.model.toSaveCardRequest
import com.judopay.judokit.android.model.toTokenRequest
import com.judopay.judokit.android.ui.common.getLocale
import com.ravelin.cardEncryption.RavelinEncrypt
import com.ravelin.cardEncryption.callback.EncryptCallback
import com.ravelin.cardEncryption.model.CardDetails
import com.ravelin.cardEncryption.model.EncryptError
import com.ravelin.cardEncryption.model.EncryptedCard
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.await
import java.util.WeakHashMap

interface ActivityAwareComponent {
    fun updateActivity(activity: FragmentActivity)
}

open class SingletonHolder<out T : ActivityAwareComponent>(creator: (FragmentActivity) -> T) {

    private var creator: ((FragmentActivity) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: FragmentActivity): T {
        val checkInstance = instance
        if (checkInstance != null) {
            checkInstance.updateActivity(arg)
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain.updateActivity(arg)
                checkInstanceAgain
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

interface CardTransactionManagerResultListener {
    fun onCardTransactionResult(result: JudoPaymentResult)
}

enum class TransactionType {
    PAYMENT,
    PRE_AUTH,
    PAYMENT_WITH_TOKEN,
    PRE_AUTH_WITH_TOKEN,
    SAVE,
    CHECK,
    REGISTER
}

private const val THREE_DS_TWO_MIN_TIMEOUT = 5

class CardTransactionManager private constructor(private var context: FragmentActivity) : ActivityAwareComponent {

    private lateinit var judo: Judo
    private lateinit var judoApiService: JudoApiService
    private lateinit var ravelinApiService: RavelinApiService
    private var threeDS2Service: ThreeDS2Service = ThreeDS2ServiceImpl()

    private var transaction: Transaction? = null
    private var transactionDetails: TransactionDetails? = null

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val parameters = ConfigParameters()
    private val locale = getLocale(context.resources)

    private val listenerMap = WeakHashMap<String, CardTransactionManagerResultListener>()
    private val results = HashMap<String, JudoPaymentResult>()

    companion object : SingletonHolder<CardTransactionManager>(::CardTransactionManager)

    override fun updateActivity(activity: FragmentActivity) {
        context = activity
    }

    fun configureWith(config: Judo) = takeIf {
        if (this::judo.isInitialized.not()) {
            return@takeIf true
        }
        config != judo
    }?.apply {
        judo = config
        judoApiService = JudoApiServiceFactory.createJudoApiService(context, config)
        ravelinApiService = JudoApiServiceFactory.createRavelinApiService(context, config)

        try {
            threeDS2Service.cleanup(context)
        } catch (e: SDKNotInitializedException) {
            Log.w(CardTransactionManager::class.java.name, "3DS2 Service not initialized.")
        }

        try {
            threeDS2Service.initialize(context, parameters, locale, judo.uiConfiguration.threeDSUiCustomization)
        } catch (e: SDKAlreadyInitializedException) {
            Log.w(CardTransactionManager::class.java.name, "3DS2 Service already initialized.")
        }
    }

    fun unRegisterResultListener(
        listener: CardTransactionManagerResultListener,
        caller: String = listener::class.java.name
    ) {
        listenerMap.remove(caller)
    }

    fun registerResultListener(
        listener: CardTransactionManagerResultListener,
        caller: String = listener::class.java.name
    ) {
        listenerMap[caller] = listener

        results[caller]?.let {
            listener.onCardTransactionResult(it)
            results.remove(caller)
        }
    }

    private fun performJudoApiRequest(
        type: TransactionType,
        details: TransactionDetails,
        transaction: Transaction
    ) = when (type) {
        TransactionType.PAYMENT -> {
            val request = details.toPaymentRequest(judo, transaction)
            judoApiService.payment(request)
        }
        TransactionType.PRE_AUTH -> {
            val request = details.toPreAuthRequest(judo, transaction)
            judoApiService.preAuthPayment(request)
        }
        TransactionType.PAYMENT_WITH_TOKEN -> {
            val request = details.toTokenRequest(judo, transaction)
            judoApiService.tokenPayment(request)
        }
        TransactionType.PRE_AUTH_WITH_TOKEN -> {
            val request = details.toPreAuthTokenRequest(judo, transaction)
            judoApiService.preAuthTokenPayment(request)
        }
        TransactionType.SAVE -> {
            val request = details.toSaveCardRequest(judo, transaction)
            judoApiService.saveCard(request)
        }
        TransactionType.CHECK -> {
            val request = details.toCheckCardRequest(judo, transaction)
            judoApiService.checkCard(request)
        }
        TransactionType.REGISTER -> {
            val request = details.toRegisterCardRequest(judo, transaction)
            judoApiService.registerCard(request)
        }
    }

    private fun performCardEncryption(details: TransactionDetails, rsaKey: String?): EncryptedCard? {
        if (judo.rsaKey == null) throw IllegalStateException("RSA key is required")
        val cardDetails = details.toEncryptCardRequest()

        val result = RavelinEncrypt().encryptCard(cardDetails, rsaKey!!,
            object : EncryptCallback<EncryptedCard>() {
                override fun failure(error: EncryptError) {
                    // Todo
                    Log.d("TESTO", error.message.toString())
                }
                override fun success(result: EncryptedCard?) {}
            })
        return result
    }

//    private fun performRavelinApiRequest(
//        details: TransactionDetails
//    ): Call<JudoApiCallResult<RavelinEncryptionResponse>> {
//        val request = details.toEncryptCardRequest(judo)
//        return ravelinApiService.encryptCard("", request)
//    }

    private fun performTransaction(
        type: TransactionType,
        details: TransactionDetails,
        caller: String
    ) = try {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.d(CardTransactionManager::class.java.name, "Uncaught 3DS2 Exception", throwable)
            dispatchException(throwable, caller)
        }

        applicationScope.launch(Dispatchers.Default + coroutineExceptionHandler) {

            // Todo: Update this check for all types that require Ravelin encryption.
            if (judo.isRavelinEncryptionEnabled && type == TransactionType.PAYMENT) {
                val encryptedCardDetails = performCardEncryption(details, judo.rsaKey)
                Log.d("TESTO 2", encryptedCardDetails.toString())
            }

            try {
                Log.d(CardTransactionManager::class.java.name, "initialize 3DS2 SDK")
                threeDS2Service.initialize(context, parameters, locale, judo.uiConfiguration.threeDSUiCustomization)
            } catch (e: SDKAlreadyInitializedException) {
                // This shouldn't cause any side effect.
                Log.w(CardTransactionManager::class.java.name, "3DS2 Service already initialized.")
            }

            val network = details.cardType ?: CardNetwork.OTHER

            val directoryServerID = when {
                judo.isSandboxed -> "F000000000"
                network == CardNetwork.VISA -> "A000000003"
                network == CardNetwork.MASTERCARD || network == CardNetwork.MAESTRO -> "A000000004"
                network == CardNetwork.AMEX -> "A000000025"
                else -> "unknown-id"
            }

            val myTransaction =
                threeDS2Service.createTransaction(directoryServerID, judo.threeDSTwoMessageVersion)

            val apiResult = performJudoApiRequest(type, details, myTransaction).await()

            transactionDetails = details
            transaction = myTransaction

            handleApiResult(apiResult, caller)
        }
    } catch (exception: Throwable) {
        dispatchException(exception, caller)
    }

    private fun onResult(result: JudoPaymentResult, caller: String) {
        results[caller] = result

        listenerMap[caller]?.let {
            it.onCardTransactionResult(result)
            results.remove(caller)
        }

        closeTransaction(context)
    }

    private fun dispatchException(throwable: Throwable, caller: String) {
        val error = JudoError.judoInternalError(throwable.message)
        onResult(JudoPaymentResult.Error(error), caller)
    }

    private fun performComplete3ds2(receipt: Receipt, caller: String) {
        val receiptId = receipt.receiptId ?: ""
        val version = receipt.getCReqParameters()?.messageVersion ?: judo.threeDSTwoMessageVersion
        val cv2 = transactionDetails?.securityNumber

        applicationScope.launch {
            val result =
                judoApiService.complete3ds2(receiptId, Complete3DS2Request(version, cv2)).await()
            onResult(result.toJudoPaymentResult(context.resources), caller)
        }
    }

    private fun handleApiResult(result: JudoApiCallResult<Receipt>, caller: String) =
        when (result) {
            is JudoApiCallResult.Failure -> {
                onResult(result.toJudoPaymentResult(context.resources), caller)
            }
            is JudoApiCallResult.Success -> if (result.data != null) {
                val receipt = result.data
                when {
                    receipt.isThreeDSecureTwoRequired -> handleThreeDSecureTwo(receipt, caller)
                    else -> onResult(result.toJudoPaymentResult(context.resources), caller)
                }
            } else {
                onResult(result.toJudoPaymentResult(context.resources), caller)
            }
        }

    private fun handleThreeDSecureTwo(receipt: Receipt, caller: String) {
        val challengeStatusReceiver = object : ChallengeStatusReceiver {
            override fun cancelled() {
                performComplete3ds2(receipt, caller)
            }

            override fun completed(completionEvent: CompletionEvent) {
                performComplete3ds2(receipt, caller)
            }

            override fun protocolError(protocolErrorEvent: ProtocolErrorEvent) {
                performComplete3ds2(receipt, caller)
            }

            override fun runtimeError(runtimeErrorEvent: RuntimeErrorEvent) {
                performComplete3ds2(receipt, caller)
            }

            override fun timedout() {
                performComplete3ds2(receipt, caller)
            }
        }

        transaction!!.doChallenge(
            context,
            receipt.getChallengeParameters(),
            challengeStatusReceiver,
            THREE_DS_TWO_MIN_TIMEOUT
        )
    }

    fun payment(details: TransactionDetails, caller: String) {
        performTransaction(TransactionType.PAYMENT, details, caller)
    }

    fun preAuth(details: TransactionDetails, caller: String) {
        performTransaction(TransactionType.PRE_AUTH, details, caller)
    }

    fun paymentWithToken(details: TransactionDetails, caller: String) {
        performTransaction(TransactionType.PAYMENT_WITH_TOKEN, details, caller)
    }

    fun preAuthWithToken(details: TransactionDetails, caller: String) {
        performTransaction(TransactionType.PRE_AUTH_WITH_TOKEN, details, caller)
    }

    fun save(details: TransactionDetails, caller: String) {
        performTransaction(TransactionType.SAVE, details, caller)
    }

    fun check(details: TransactionDetails, caller: String) {
        performTransaction(TransactionType.CHECK, details, caller)
    }

    fun register(details: TransactionDetails, caller: String) {
        performTransaction(TransactionType.REGISTER, details, caller)
    }

    private fun closeTransaction(context: Context) {
        transaction?.close()
        transaction = null

        try {
            threeDS2Service.cleanup(context)
        } catch (exception: SDKNotInitializedException) {
            Log.w(CardTransactionManager::class.java.name, exception.toString())
        }
    }
}
