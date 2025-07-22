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
import com.judopay.judokit.android.api.model.response.recommendation.RecommendationAction
import com.judopay.judokit.android.api.model.response.recommendation.toTransactionDetailsOverrides
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.TransactionDetailsOverrides
import com.judopay.judokit.android.model.toCheckCardRequest
import com.judopay.judokit.android.model.toPaymentRequest
import com.judopay.judokit.android.model.toPreAuthRequest
import com.judopay.judokit.android.model.toPreAuthTokenRequest
import com.judopay.judokit.android.model.toRegisterCardRequest
import com.judopay.judokit.android.model.toSaveCardRequest
import com.judopay.judokit.android.model.toTokenRequest
import com.judopay.judokit.android.ui.common.getLocale
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.await
import java.util.WeakHashMap

interface ActivityAwareComponent {
    fun updateActivity(activity: FragmentActivity)
}

open class SingletonHolder<out T : ActivityAwareComponent>(
    creator: (FragmentActivity) -> T,
) {
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

    @Deprecated(
        "Register Card functionality has been deprecated and will be removed in a future version. " +
            "Please use Check Card feature instead.",
    )
    REGISTER,
}

private val TransactionType.canBeSoftDeclined: Boolean
    get() =
        arrayOf(
            TransactionType.PAYMENT,
            TransactionType.PRE_AUTH,
            TransactionType.REGISTER,
            TransactionType.PAYMENT_WITH_TOKEN,
            TransactionType.PRE_AUTH_WITH_TOKEN,
        ).contains(this)

private const val THREE_DS_TWO_MIN_TIMEOUT = 5
private const val SHOULD_USE_FABRICK_DS_ID = "shouldUseFabrickDsId"

@Suppress("TooManyFunctions", "SwallowedException", "TooGenericExceptionCaught")
class CardTransactionManager private constructor(
    private var context: FragmentActivity,
) : ActivityAwareComponent {
    private lateinit var judo: Judo
    private lateinit var judoApiService: JudoApiService
    private lateinit var recommendationService: RecommendationService

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

    fun configureWith(config: Judo) =
        takeIf {
            if (this::judo.isInitialized.not()) {
                return@takeIf true
            }
            config != judo
        }?.apply {
            judo = config
            judoApiService = JudoApiServiceFactory.create(context, config)
            recommendationService = RecommendationService(context, config)

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
        caller: String = listener::class.java.name,
    ) {
        listenerMap.remove(caller)
    }

    fun registerResultListener(
        listener: CardTransactionManagerResultListener,
        caller: String = listener::class.java.name,
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
        transaction: Transaction,
        overrides: TransactionDetailsOverrides?,
    ) = when (type) {
        TransactionType.PAYMENT -> {
            val request =
                details.toPaymentRequest(
                    judo,
                    transaction,
                    overrides,
                )
            judoApiService.payment(request)
        }
        TransactionType.PRE_AUTH -> {
            val request =
                details.toPreAuthRequest(
                    judo,
                    transaction,
                    overrides,
                )
            judoApiService.preAuthPayment(request)
        }
        TransactionType.PAYMENT_WITH_TOKEN -> {
            val request = details.toTokenRequest(judo, transaction, overrides)
            judoApiService.tokenPayment(request)
        }
        TransactionType.PRE_AUTH_WITH_TOKEN -> {
            val request = details.toPreAuthTokenRequest(judo, transaction, overrides)
            judoApiService.preAuthTokenPayment(request)
        }
        TransactionType.SAVE -> {
            val request = details.toSaveCardRequest(judo, transaction)
            judoApiService.saveCard(request)
        }
        TransactionType.CHECK -> {
            val request =
                details.toCheckCardRequest(
                    judo,
                    transaction,
                    overrides,
                )
            judoApiService.checkCard(request)
        }
        TransactionType.REGISTER -> {
            val request = details.toRegisterCardRequest(judo, transaction, overrides)
            judoApiService.registerCard(request)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun performTransaction(
        type: TransactionType,
        details: TransactionDetails,
        caller: String,
        overrides: TransactionDetailsOverrides? = null,
    ) = try {
        when {
            overrides != null ->
                performJudoApiCall(
                    type,
                    details,
                    caller,
                    overrides,
                ) // soft decline case - we don't need to call recommendation service
            recommendationService.isRecommendationFeatureAvailable(
                type,
            ) -> apply3DS2Optimisations(type, details, caller) // recommendation service is available, attempt to call it
            else -> performJudoApiCall(type, details, caller) // fallback to default Judo API call
        }
    } catch (exception: Throwable) {
        dispatchException(exception, caller)
    }

    private fun apply3DS2Optimisations(
        type: TransactionType,
        details: TransactionDetails,
        caller: String,
    ) = try {
        val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.d(CardTransactionManager::class.java.name, "Uncaught Recommendation service exception", throwable)
                handleRecommendationError(type, details, caller)
            }

        applicationScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
            val recommendation = recommendationService.fetchOptimizationData(details, type).await()

            if (recommendation.isValid) {
                when (recommendation.data?.action) {
                    RecommendationAction.PREVENT -> {
                        val error = JudoError.judoRecommendationTransactionPreventedError(context.resources)
                        val paymentResult = JudoPaymentResult.Error(error)

                        onResult(paymentResult, caller)
                    }
                    else -> performJudoApiCall(type, details, caller, recommendation.toTransactionDetailsOverrides())
                }
            } else {
                handleRecommendationError(type, details, caller)
            }
        }
    } catch (exception: Throwable) {
        handleRecommendationError(type, details, caller)
    }

    private fun handleRecommendationError(
        type: TransactionType,
        details: TransactionDetails,
        caller: String,
    ) {
        val shouldHaltTransaction = judo.recommendationConfiguration?.shouldHaltTransactionInCaseOfAnyError ?: false
        if (shouldHaltTransaction) {
            val error = JudoError.judoRecommendationRetrievingError(context.resources)
            val result = JudoPaymentResult.Error(error)

            onResult(result, caller)
        } else {
            performJudoApiCall(type, details, caller)
        }
    }

    private fun performJudoApiCall(
        type: TransactionType,
        details: TransactionDetails,
        caller: String,
        overrides: TransactionDetailsOverrides? = null,
    ) {
        try {
            val coroutineExceptionHandler =
                CoroutineExceptionHandler { _, throwable ->
                    Log.d(CardTransactionManager::class.java.name, "Uncaught 3DS2 Exception", throwable)
                    dispatchException(throwable, caller)
                }

            applicationScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
                try {
                    Log.d(CardTransactionManager::class.java.name, "initialize 3DS2 SDK")
                    threeDS2Service.initialize(
                        context,
                        parameters,
                        locale,
                        judo.uiConfiguration.threeDSUiCustomization,
                    )
                } catch (e: SDKAlreadyInitializedException) {
                    // This shouldn't cause any side effect.
                    Log.w(
                        CardTransactionManager::class.java.name,
                        "3DS2 Service already initialized.",
                    )
                }

                val network = details.cardType ?: CardNetwork.OTHER
                val sandboxDSID =
                    if (judo.extras.getBoolean(SHOULD_USE_FABRICK_DS_ID, false)) {
                        "F121535344"
                    } else {
                        "F000000000"
                    }

                val directoryServerID =
                    when {
                        judo.isSandboxed -> sandboxDSID
                        network == CardNetwork.VISA -> "A000000003"
                        network == CardNetwork.MASTERCARD || network == CardNetwork.MAESTRO -> "A000000004"
                        network == CardNetwork.AMEX -> "A000000025"
                        else -> "unknown-id"
                    }

                val myTransaction =
                    threeDS2Service.createTransaction(
                        directoryServerID,
                        judo.threeDSTwoMessageVersion,
                    )

                val apiResult =
                    performJudoApiRequest(
                        type,
                        details,
                        myTransaction,
                        overrides,
                    ).await()

                transactionDetails = details
                transaction = myTransaction

                handleJudoApiResult(type, details, caller, apiResult)
            }
        } catch (exception: Throwable) {
            dispatchException(exception, caller)
        }
    }

    private fun onResult(
        result: JudoPaymentResult,
        caller: String,
    ) {
        results[caller] = result

        listenerMap[caller]?.let {
            it.onCardTransactionResult(result)
            results.remove(caller)
        }

        closeTransaction(context)
    }

    private fun dispatchException(
        throwable: Throwable,
        caller: String,
    ) {
        val error = JudoError.judoInternalError(throwable.message)
        onResult(JudoPaymentResult.Error(error), caller)
    }

    private fun performComplete3ds2(
        receipt: Receipt,
        caller: String,
        threeDSSDKChallengeStatus: String? = null,
    ) {
        val receiptId = receipt.receiptId ?: ""
        val version = receipt.getCReqParameters()?.messageVersion ?: judo.threeDSTwoMessageVersion
        val cv2 = transactionDetails?.securityNumber

        applicationScope.launch {
            val result =
                judoApiService.complete3ds2(receiptId, Complete3DS2Request(version, cv2, threeDSSDKChallengeStatus)).await()
            onResult(result.toJudoPaymentResult(context.resources), caller)
        }
    }

    private fun handleJudoApiResult(
        type: TransactionType,
        details: TransactionDetails,
        caller: String,
        result: JudoApiCallResult<Receipt>,
    ) = when (result) {
        is JudoApiCallResult.Failure -> {
            onResult(
                (result as JudoApiCallResult<Receipt>).toJudoPaymentResult(context.resources),
                caller,
            )
        }
        is JudoApiCallResult.Success ->
            if (result.data != null) {
                val receipt = result.data
                when {
                    type.canBeSoftDeclined && receipt.isSoftDeclined -> handleStepUpFlow(type, details, caller, receipt.receiptId!!)
                    receipt.isThreeDSecureTwoRequired -> handleThreeDSecureTwo(receipt, caller)
                    else -> onResult(result.toJudoPaymentResult(context.resources), caller)
                }
            } else {
                onResult(result.toJudoPaymentResult(context.resources), caller)
            }
    }

    private fun handleThreeDSecureTwo(
        receipt: Receipt,
        caller: String,
    ) {
        val challengeStatusReceiver =
            object : ChallengeStatusReceiver {
                override fun cancelled() {
                    performComplete3ds2(receipt, caller, ThreeDSSDKChallengeStatus.CANCELLED)
                }

                override fun completed(completionEvent: CompletionEvent) {
                    performComplete3ds2(receipt, caller, completionEvent.toFormattedEventString())
                }

                override fun protocolError(protocolErrorEvent: ProtocolErrorEvent) {
                    performComplete3ds2(receipt, caller, protocolErrorEvent.toFormattedEventString())
                }

                override fun runtimeError(runtimeErrorEvent: RuntimeErrorEvent) {
                    performComplete3ds2(receipt, caller, runtimeErrorEvent.toFormattedEventString())
                }

                override fun timedout() {
                    performComplete3ds2(receipt, caller, ThreeDSSDKChallengeStatus.TIMEOUT)
                }
            }

        transaction!!.doChallenge(
            context,
            receipt.getChallengeParameters(),
            challengeStatusReceiver,
            THREE_DS_TWO_MIN_TIMEOUT,
        )
    }

    fun payment(
        details: TransactionDetails,
        caller: String,
    ) {
        performTransaction(TransactionType.PAYMENT, details, caller)
    }

    fun preAuth(
        details: TransactionDetails,
        caller: String,
    ) {
        performTransaction(TransactionType.PRE_AUTH, details, caller)
    }

    fun paymentWithToken(
        details: TransactionDetails,
        caller: String,
    ) {
        performTransaction(TransactionType.PAYMENT_WITH_TOKEN, details, caller)
    }

    fun preAuthWithToken(
        details: TransactionDetails,
        caller: String,
    ) {
        performTransaction(TransactionType.PRE_AUTH_WITH_TOKEN, details, caller)
    }

    fun save(
        details: TransactionDetails,
        caller: String,
    ) {
        performTransaction(TransactionType.SAVE, details, caller)
    }

    fun check(
        details: TransactionDetails,
        caller: String,
    ) {
        performTransaction(TransactionType.CHECK, details, caller)
    }

    fun register(
        details: TransactionDetails,
        caller: String,
    ) {
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

    private fun handleStepUpFlow(
        type: TransactionType,
        details: TransactionDetails,
        caller: String,
        softDeclineReceiptId: String,
    ) {
        closeTransaction(context)

        val overrides =
            TransactionDetailsOverrides(
                softDeclineReceiptId = softDeclineReceiptId,
                challengeRequestIndicator = ChallengeRequestIndicator.CHALLENGE_AS_MANDATE,
            )
        performTransaction(type, details, caller, overrides)
    }
}
