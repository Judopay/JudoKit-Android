package com.judopay.judokit.android.service

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.judopay.judo3ds2.exception.InvalidInputException
import com.judopay.judo3ds2.exception.SDKAlreadyInitializedException
import com.judopay.judo3ds2.exception.SDKNotInitializedException
import com.judopay.judo3ds2.model.ConfigParameters
import com.judopay.judo3ds2.service.ThreeDS2Service
import com.judopay.judo3ds2.service.ThreeDS2ServiceImpl
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import com.judopay.judo3ds2.ui.challenge.model.ChallengeExtra
import com.judopay.judo3ds2.ui.challenge.model.toChallengeExtra
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
import com.judopay.judokit.android.model.toSaveCardRequest
import com.judopay.judokit.android.model.toTokenRequest
import com.judopay.judokit.android.ui.common.getLocale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.await

/**
 * A suspend-function-based callback for performing the native 3DS2 challenge.
 *
 * The caller (typically a Fragment) receives the [ChallengeData], launches
 * [ChallengeData.challengeExtra] via
 * [com.judopay.judo3ds2.ui.challenge.ChallengeActivityResultContract], and returns
 * the raw `threeDSSDKChallengeStatus` string to forward to the
 * `complete3ds2` API endpoint.
 */
internal typealias ChallengeRunner = suspend (ChallengeData) -> String?

/**
 * The type of card transaction being performed.
 *
 * Used internally by [CardTransactionRepository] and [RecommendationService]
 * to select the correct API endpoint and optimisation strategy.
 */
internal enum class TransactionType {
    PAYMENT,
    PRE_AUTH,
    PAYMENT_WITH_TOKEN,
    PRE_AUTH_WITH_TOKEN,
    SAVE,
    CHECK,
}

private val TransactionType.canBeSoftDeclined: Boolean
    get() =
        this == TransactionType.PAYMENT ||
            this == TransactionType.PRE_AUTH ||
            this == TransactionType.PAYMENT_WITH_TOKEN ||
            this == TransactionType.PRE_AUTH_WITH_TOKEN

/** Minimum 3DS2 challenge timeout in minutes, as required by the EMV specification. */
internal const val THREE_DS_TWO_MIN_TIMEOUT = 5

private const val SHOULD_USE_FABRICK_DS_ID = "shouldUseFabrickDsId"

/**
 * What a [ChallengeRunner] needs to run a 3DS2 native challenge.
 *
 * [toChallengeExtra] builds what [com.judopay.judo3ds2.ui.challenge.ChallengeActivityResultContract]
 * launches. [transaction] and [challengeParameters] are also used directly by the deprecated
 * [Transaction.doChallenge] path of the `Context`-based
 * [com.judopay.judokit.android.JudoCardTransactionClient] methods, which never builds the extra.
 *
 * [receiptId] and [messageVersion] are what [CardTransactionRepository.complete3ds2] needs to finish
 * the transaction once the challenge ends. Neither is card data, so callers may persist them to
 * finish the transaction after process death.
 */
internal data class ChallengeData(
    val transaction: Transaction,
    val challengeParameters: ChallengeParameters,
    val directoryServerId: String,
    val receiptId: String,
    val messageVersion: String,
) {
    /**
     * Builds the [ChallengeExtra] to launch. `getAuthenticationRequestParameters()` returns the
     * instance already sent in the request, so it carries the SDK key pair the ACS derived its
     * secret from.
     *
     * @throws IllegalArgumentException if the challenge parameters miss a required value.
     */
    fun toChallengeExtra(): ChallengeExtra =
        transaction
            .getAuthenticationRequestParameters()
            .toChallengeExtra(challengeParameters, THREE_DS_TWO_MIN_TIMEOUT, directoryServerId)
}

/**
 * Executes card transactions (payment, pre-auth, save, check, token variants)
 * as structured coroutines with no singleton state.
 *
 * This is created by the Fragment via [CardTransactionRepository.create].
 *
 * 3DS2 challenges are delegated to the caller through the [ChallengeRunner]
 * functional type, keeping this class free of [android.app.Activity] references.
 *
 * @param judo          The immutable Judo configuration for this payment session.
 * @param judoApiService The Retrofit API service.
 * @param threeDS2Service The 3DS2 SDK service instance (not yet initialized).
 * @param recommendationService Service that may short-circuit 3DS2 via Ravelin.
 * @param dsCertificateRepository Provides dynamically fetched DS certificates from the CDN
 *   cache. The [DsCertificateRepository.cachedEntry] lookup is cache-only — it never blocks
 *   the transaction path on the network.
 * @param resources     Android [Resources], used for locale and error messages.
 * @param context       Application context used for 3DS2 initialization/cleanup.
 */
@Suppress("TooManyFunctions")
internal class CardTransactionRepository
    @Suppress("LongParameterList")
    internal constructor(
        private val judo: Judo,
        private val judoApiService: JudoApiService,
        private val threeDS2Service: ThreeDS2Service,
        private val recommendationService: RecommendationService,
        private val dsCertificateRepository: DsCertificateRepository,
        private val resources: Resources,
        private val context: Context,
    ) {
        private val parameters = ConfigParameters()
        private val locale = getLocale(resources)

        suspend fun payment(
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult = performTransaction(TransactionType.PAYMENT, details, runChallenge)

        suspend fun preAuth(
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult = performTransaction(TransactionType.PRE_AUTH, details, runChallenge)

        suspend fun paymentWithToken(
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult = performTransaction(TransactionType.PAYMENT_WITH_TOKEN, details, runChallenge)

        suspend fun preAuthWithToken(
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult = performTransaction(TransactionType.PRE_AUTH_WITH_TOKEN, details, runChallenge)

        suspend fun save(
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult = performTransaction(TransactionType.SAVE, details, runChallenge)

        suspend fun check(
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult = performTransaction(TransactionType.CHECK, details, runChallenge)

        @Suppress("TooGenericExceptionCaught")
        private suspend fun performTransaction(
            type: TransactionType,
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
            overrides: TransactionDetailsOverrides? = null,
        ): JudoPaymentResult =
            try {
                when {
                    overrides != null ->
                        performApiCall(type, details, runChallenge, overrides)
                    recommendationService.isRecommendationFeatureAvailable(type) ->
                        applyRecommendationOptimisations(type, details, runChallenge)
                    else ->
                        performApiCall(type, details, runChallenge)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                JudoPaymentResult.Error(JudoError.judoInternalError(e.message))
            }

        @Suppress("TooGenericExceptionCaught")
        private suspend fun applyRecommendationOptimisations(
            type: TransactionType,
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult =
            try {
                val recommendation = recommendationService.fetchOptimizationData(details, type).await()
                if (recommendation.isValid) {
                    when (recommendation.data?.action) {
                        RecommendationAction.PREVENT ->
                            JudoPaymentResult.Error(
                                JudoError.judoRecommendationTransactionPreventedError(resources),
                            )
                        else ->
                            performApiCall(
                                type,
                                details,
                                runChallenge,
                                recommendation.toTransactionDetailsOverrides(),
                            )
                    }
                } else {
                    handleRecommendationError(type, details, runChallenge)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.d(TAG, "Uncaught Recommendation service exception", e)
                handleRecommendationError(type, details, runChallenge)
            }

        private suspend fun handleRecommendationError(
            type: TransactionType,
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult {
            val shouldHalt = judo.recommendationConfiguration?.shouldHaltTransactionInCaseOfAnyError ?: false
            return if (shouldHalt) {
                JudoPaymentResult.Error(JudoError.judoRecommendationRetrievingError(resources))
            } else {
                performApiCall(type, details, runChallenge)
            }
        }

        @Suppress("TooGenericExceptionCaught")
        private suspend fun performApiCall(
            type: TransactionType,
            details: TransactionDetails,
            runChallenge: ChallengeRunner,
            overrides: TransactionDetailsOverrides? = null,
        ): JudoPaymentResult {
            try {
                threeDS2Service.initialize(context, parameters, locale, judo.uiConfiguration.threeDSUiCustomization)
            } catch (e: SDKAlreadyInitializedException) {
                Log.w(TAG, "3DS2 Service already initialized.", e)
            }

            val network = details.cardType ?: CardNetwork.OTHER
            val directoryServerId = resolveDirectoryServerId(network)

            threeDS2Service.setCertificateProvider(JudoDsCertificateProvider(dsCertificateRepository))
            val transaction = threeDS2Service.createTransaction(directoryServerId, judo.threeDSTwoMessageVersion)

            return try {
                val apiResult = buildApiRequest(type, details, transaction, overrides).await()
                handleApiResult(type, details, transaction, directoryServerId, runChallenge, apiResult)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.d(TAG, "Uncaught 3DS2 Exception", e)
                JudoPaymentResult.Error(JudoError.judoInternalError(e.message))
            } finally {
                // Safe to call even if handleStepUp already closed this transaction.
                closeTransaction(transaction)
            }
        }

        @Suppress("LongParameterList")
        private suspend fun handleApiResult(
            type: TransactionType,
            details: TransactionDetails,
            transaction: Transaction,
            directoryServerId: String,
            runChallenge: ChallengeRunner,
            result: JudoApiCallResult<Receipt>,
        ): JudoPaymentResult =
            when (result) {
                is JudoApiCallResult.Failure -> result.toJudoPaymentResult(resources)
                is JudoApiCallResult.Success -> {
                    val receipt = result.data
                    when {
                        receipt == null -> result.toJudoPaymentResult(resources)
                        type.canBeSoftDeclined && receipt.isSoftDeclined ->
                            handleStepUp(type, details, transaction, runChallenge, receipt.receiptId!!)
                        receipt.isThreeDSecureTwoRequired ->
                            handleThreeDSecure2(details, receipt, transaction, directoryServerId, runChallenge)
                        else -> result.toJudoPaymentResult(resources)
                    }
                }
            }

        /**
         * Handles the soft-decline step-up flow: closes the current 3DS2 transaction,
         * then retries with [ChallengeRequestIndicator.CHALLENGE_AS_MANDATE].
         */
        private suspend fun handleStepUp(
            type: TransactionType,
            details: TransactionDetails,
            transaction: Transaction,
            runChallenge: ChallengeRunner,
            softDeclineReceiptId: String,
        ): JudoPaymentResult {
            closeTransaction(transaction)
            val overrides =
                TransactionDetailsOverrides(
                    softDeclineReceiptId = softDeclineReceiptId,
                    challengeRequestIndicator = ChallengeRequestIndicator.CHALLENGE_AS_MANDATE,
                )
            return performTransaction(type, details, runChallenge, overrides)
        }

        /**
         * Delegates the 3DS2 native challenge to [runChallenge] and then completes
         * the transaction via the `complete3ds2` API endpoint.
         */
        @Suppress("TooGenericExceptionCaught")
        private suspend fun handleThreeDSecure2(
            details: TransactionDetails,
            receipt: Receipt,
            transaction: Transaction,
            directoryServerId: String,
            runChallenge: ChallengeRunner,
        ): JudoPaymentResult {
            val receiptId = receipt.receiptId ?: ""
            val version = receipt.getCReqParameters()?.messageVersion ?: judo.threeDSTwoMessageVersion
            // Whatever goes wrong running the challenge, complete3ds2 is still sent so the backend
            // learns how the challenge ended, as the ChallengeStatusReceiver.runtimeError path did.
            val challengeStatus =
                try {
                    runChallenge(ChallengeData(transaction, receipt.getChallengeParameters(), directoryServerId, receiptId, version))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "3DS2 challenge failed to run", e)
                    runtimeErrorChallengeStatus(e.message ?: "Failed to run the 3DS2 challenge.")
                }
            return complete3ds2(receiptId, version, details.securityNumber, challengeStatus)
        }

        /**
         * Completes a 3DS2 transaction after its challenge via the `complete3ds2` API endpoint.
         *
         * Also called directly when a challenge outcome arrives after process death, when the
         * original [performTransaction] coroutine no longer exists; [cv2] is then `null`, since card
         * data is never persisted.
         */
        @Suppress("TooGenericExceptionCaught")
        suspend fun complete3ds2(
            receiptId: String,
            messageVersion: String,
            cv2: String?,
            challengeStatus: String?,
        ): JudoPaymentResult =
            try {
                judoApiService
                    .complete3ds2(receiptId, Complete3DS2Request(messageVersion, cv2, challengeStatus))
                    .await()
                    .toJudoPaymentResult(resources)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                JudoPaymentResult.Error(JudoError.judoInternalError(e.message))
            }

        private fun buildApiRequest(
            type: TransactionType,
            details: TransactionDetails,
            transaction: Transaction,
            overrides: TransactionDetailsOverrides?,
        ) = when (type) {
            TransactionType.PAYMENT ->
                judoApiService.payment(details.toPaymentRequest(judo, transaction, overrides))
            TransactionType.PRE_AUTH ->
                judoApiService.preAuthPayment(details.toPreAuthRequest(judo, transaction, overrides))
            TransactionType.PAYMENT_WITH_TOKEN ->
                judoApiService.tokenPayment(details.toTokenRequest(judo, transaction, overrides))
            TransactionType.PRE_AUTH_WITH_TOKEN ->
                judoApiService.preAuthTokenPayment(details.toPreAuthTokenRequest(judo, transaction, overrides))
            TransactionType.SAVE ->
                judoApiService.saveCard(details.toSaveCardRequest(judo, transaction))
            TransactionType.CHECK ->
                judoApiService.checkCard(details.toCheckCardRequest(judo, transaction, overrides))
        }

        private fun resolveDirectoryServerId(network: CardNetwork): String {
            val sandboxDsId =
                if (judo.extras.getBoolean(SHOULD_USE_FABRICK_DS_ID, false)) "F121535344" else "F000000000"
            return when {
                judo.isSandboxed -> sandboxDsId
                network == CardNetwork.VISA -> "A000000003"
                network == CardNetwork.MASTERCARD || network == CardNetwork.MAESTRO -> "A000000004"
                network == CardNetwork.AMEX -> "A000000025"
                else -> "unknown-id"
            }
        }

        /**
         * Closes the 3DS2 [Transaction] and cleans up the [ThreeDS2Service].
         * Safe to call multiple times — subsequent calls are no-ops.
         */
        private fun closeTransaction(transaction: Transaction) {
            try {
                transaction.close()
            } catch (e: InvalidInputException) {
                Log.w(TAG, "3DS2 transaction already closed or in invalid state.", e)
            }
            try {
                threeDS2Service.cleanup(context)
            } catch (e: SDKNotInitializedException) {
                Log.w(TAG, "3DS2 Service not initialized when attempting cleanup.", e)
            }
        }

        companion object {
            private val TAG = CardTransactionRepository::class.java.name

            /**
             * Creates a fully configured [CardTransactionRepository] for the given [judo] configuration.
             * Use this factory in Fragment `initializeViewModel()` calls.
             */
            fun create(
                context: Context,
                judo: Judo,
            ): CardTransactionRepository {
                val appContext = context.applicationContext
                val dsCertificateRepository = DsCertificateRepository.getInstance(appContext, judo)
                CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                    dsCertificateRepository.prefetch()
                }
                return CardTransactionRepository(
                    judo = judo,
                    judoApiService = JudoApiServiceFactory.create(appContext, judo),
                    threeDS2Service = ThreeDS2ServiceImpl(),
                    recommendationService = RecommendationService(appContext, judo),
                    dsCertificateRepository = dsCertificateRepository,
                    resources = appContext.resources,
                    context = appContext,
                )
            }
        }
    }
