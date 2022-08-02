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
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.request.Complete3DS2Request
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.getCReqParameters
import com.judopay.judokit.android.api.model.response.getChallengeParameters
import com.judopay.judokit.android.api.model.response.toCardVerificationModel
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.REQUEST_FAILED
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.toCheckCardRequest
import com.judopay.judokit.android.model.toPaymentRequest
import com.judopay.judokit.android.model.toRegisterCardRequest
import com.judopay.judokit.android.model.toSaveCardRequest
import com.judopay.judokit.android.model.toTokenRequest
import com.judopay.judokit.android.ui.cardverification.THREE_DS_ONE_DIALOG_FRAGMENT_TAG
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCardVerificationDialogFragment
import com.judopay.judokit.android.ui.cardverification.ThreeDSOneCompletionCallback
import com.judopay.judokit.android.ui.common.getLocale
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.WeakHashMap
import kotlin.collections.HashMap

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
    private lateinit var apiService: JudoApiService
    private var threeDS2Service: ThreeDS2Service = ThreeDS2ServiceImpl()

    private var transaction: Transaction? = null
    private var receipt: Receipt? = null
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

    init {
        threeDS2Service.initialize(context, parameters, locale, null)
    }

    fun configureWith(config: Judo) = takeIf {
        if (this::judo.isInitialized.not()) {
            return@takeIf true
        }
        config != judo
    }?.apply {
        judo = config
        apiService = JudoApiServiceFactory.createApiService(context, config)
    }

    public fun unRegisterResultListener(
        listener: CardTransactionManagerResultListener,
        caller: String = listener::class.java.name
    ) {
        listenerMap.remove(caller)
    }

    public fun registerResultListener(
        listener: CardTransactionManagerResultListener,
        caller: String = listener::class.java.name
    ) {
        listenerMap[caller] = listener

        results[caller]?.let {
            listener.onCardTransactionResult(it)
            results.remove(caller)
        }
    }

    private fun performApiRequest(
        type: TransactionType,
        details: TransactionDetails,
        transaction: Transaction
    ) = when (type) {
        TransactionType.PAYMENT -> {
            val request = details.toPaymentRequest(judo, transaction)
            apiService.payment(request)
        }
        TransactionType.PRE_AUTH -> {
            val request = details.toPaymentRequest(judo, transaction)
            apiService.preAuthPayment(request)
        }
        TransactionType.PAYMENT_WITH_TOKEN -> {
            val request = details.toTokenRequest(judo, transaction)
            apiService.tokenPayment(request)
        }
        TransactionType.PRE_AUTH_WITH_TOKEN -> {
            val request = details.toTokenRequest(judo, transaction)
            apiService.preAuthTokenPayment(request)
        }
        TransactionType.SAVE -> {
            val request = details.toSaveCardRequest(judo, transaction)
            apiService.saveCard(request)
        }
        TransactionType.CHECK -> {
            val request = details.toCheckCardRequest(judo, transaction)
            apiService.checkCard(request)
        }
        TransactionType.REGISTER -> {
            val request = details.toRegisterCardRequest(judo, transaction)
            apiService.registerCard(request)
        }
    }

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
            try {
                Log.d(CardTransactionManager::class.java.name, "initialize 3DS2 SDK")
                threeDS2Service.initialize(context, parameters, locale, null)
            } catch (e: SDKAlreadyInitializedException) {
                // This shouldn't cause any side effect.
                Log.w(CardTransactionManager::class.java.name, "3DS2 Service already initialized.")
            }

            val network = details.cardType ?: CardNetwork.OTHER

            val directoryServerID = when {
                judo.isSandboxed -> "F000000000"
                network == CardNetwork.VISA -> "A000000003"
                network == CardNetwork.MASTERCARD -> "A000000004"
                network == CardNetwork.AMEX -> "A000000025"
                else -> "unknown-id"
            }

            val myTransaction =
                threeDS2Service.createTransaction(directoryServerID, judo.threeDSTwoMessageVersion)

            val apiResult = performApiRequest(type, details, myTransaction).await()

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
        val cv2 = transactionDetails?.securityNumber ?: ""

        applicationScope.launch {
            val result =
                apiService.complete3ds2(receiptId, Complete3DS2Request(version, cv2)).await()
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
                    receipt.isThreeDSecureOneRequired -> handleThreeDSecureOne(receipt, caller)
                    receipt.isThreeDSecureTwoRequired -> handleThreeDSecureTwo(receipt, caller)
                    else -> onResult(result.toJudoPaymentResult(context.resources), caller)
                }
            } else {
                onResult(result.toJudoPaymentResult(context.resources), caller)
            }
        }

    private fun handleThreeDSecureOne(receipt: Receipt, caller: String) {
        Log.d("Manager1", context.supportFragmentManager.toString())
        val cardVerificationModel = receipt.toCardVerificationModel()
        val threeDSOneCompletionCallback = object : ThreeDSOneCompletionCallback {

            override fun onSuccess(success: JudoPaymentResult) {
                val result = success as JudoPaymentResult.Success
                onResult(result, caller)
            }

            override fun onFailure(error: JudoPaymentResult) {
                onResult(error, caller)
            }
        }

        val fragment = ThreeDSOneCardVerificationDialogFragment(
            apiService,
            cardVerificationModel,
            threeDSOneCompletionCallback
        )

        fragment.show(context.supportFragmentManager, THREE_DS_ONE_DIALOG_FRAGMENT_TAG)
    }

    private fun handleThreeDSecureTwo(receipt: Receipt, caller: String) {
        val challengeStatusReceiver = object : ChallengeStatusReceiver {
            override fun cancelled() {
                val result = JudoPaymentResult.UserCancelled()
                onResult(result, caller)
            }

            override fun completed(completionEvent: CompletionEvent) {
                performComplete3ds2(receipt, caller)
            }

            override fun protocolError(protocolErrorEvent: ProtocolErrorEvent) {
                val result = JudoPaymentResult.Error(
                    JudoError(
                        protocolErrorEvent.errorMessage.errorCode.toIntOrNull()
                            ?: REQUEST_FAILED,
                        protocolErrorEvent.errorMessage.errorDescription
                    )
                )

                onResult(result, caller)
            }

            override fun runtimeError(runtimeErrorEvent: RuntimeErrorEvent) {
                val result = JudoPaymentResult.Error(
                    JudoError(
                        runtimeErrorEvent.errorCode?.toIntOrNull() ?: REQUEST_FAILED,
                        runtimeErrorEvent.errorMessage
                    )
                )

                onResult(result, caller)
            }

            override fun timedout() {
                onResult(JudoPaymentResult.Error(JudoError(REQUEST_FAILED, "Request timed out.")), caller)
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

    fun cleanup() {
        threeDS2Service.cleanup(context)
        applicationScope.coroutineContext.cancel()
    }
}
