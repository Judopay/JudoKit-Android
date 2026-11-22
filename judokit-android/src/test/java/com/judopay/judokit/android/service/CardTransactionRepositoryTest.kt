package com.judopay.judokit.android.service

import android.content.Context
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.judopay.judo3ds2.service.ThreeDS2Service
import com.judopay.judo3ds2.transaction.AuthenticationRequestParameters
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import com.judopay.judo3ds2.ui.challenge.model.ChallengeExtra
import com.judopay.judo3ds2.ui.challenge.model.toChallengeExtra
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.request.Complete3DS2Request
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import com.judopay.judokit.android.api.model.response.getCReqParameters
import com.judopay.judokit.android.api.model.response.getChallengeParameters
import com.judopay.judokit.android.api.model.response.recommendation.RecommendationAction
import com.judopay.judokit.android.api.model.response.recommendation.RecommendationData
import com.judopay.judokit.android.api.model.response.recommendation.RecommendationResponse
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.RecommendationConfiguration
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.toPaymentRequest
import com.judopay.judokit.android.model.toPreAuthTokenRequest
import com.judopay.judokit.android.model.toTokenRequest
import com.judopay.judokit.android.service.DsCertificateRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.await

@ExperimentalCoroutinesApi
@DisplayName("Testing CardTransactionRepository")
internal class CardTransactionRepositoryTest {
    private val judo: Judo = mockk(relaxed = true)
    private val judoApiService: JudoApiService = mockk(relaxed = true)
    private val threeDS2Service: ThreeDS2Service = mockk(relaxed = true)
    private val recommendationService: RecommendationService = mockk(relaxed = true)
    private val dsCertificateRepository: DsCertificateRepository = mockk(relaxed = true)
    private val resources: Resources = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val transaction: Transaction = mockk(relaxed = true)

    private lateinit var sut: CardTransactionRepository

    @BeforeEach
    fun setUp() {
        mockkStatic("androidx.core.os.ConfigurationCompat")
        mockkStatic("retrofit2.KotlinExtensions")
        mockkStatic("com.judopay.judokit.android.api.model.response.JudoApiCallResultKt")
        mockkStatic("com.judopay.judokit.android.model.TransactionDetailsKt")

        every { ConfigurationCompat.getLocales(any()) } returns LocaleListCompat.getEmptyLocaleList()
        every { recommendationService.isRecommendationFeatureAvailable(any()) } returns false
        every { threeDS2Service.createTransaction(any(), any()) } returns transaction

        sut =
            CardTransactionRepository(
                judo,
                judoApiService,
                threeDS2Service,
                recommendationService,
                dsCertificateRepository,
                resources,
                context,
            )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Nested
    @DisplayName("payment")
    inner class PaymentTests {
        @DisplayName("When createTransaction throws, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenCreateTransactionThrows() =
            runTest {
                every { threeDS2Service.createTransaction(any(), any()) } throws RuntimeException("SDK error")

                val result = sut.payment(mockk(relaxed = true)) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }

        @DisplayName("When API call returns Failure, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenApiCallReturnsFail() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                val failureResult = JudoApiCallResult.Failure()
                val expectedPaymentResult = JudoPaymentResult.Error(mockk(relaxed = true))

                every { details.toPaymentRequest(any(), any(), any()) } returns mockk(relaxed = true)
                every { judoApiService.payment(any()) } returns call
                coEvery { call.await() } returns failureResult
                every { failureResult.toJudoPaymentResult(resources) } returns expectedPaymentResult

                val result = sut.payment(details) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }

        @DisplayName("When API call returns Success with non-3DS2 receipt, then JudoPaymentResult.Success is returned")
        @Test
        fun returnSuccessWhenApiCallSucceeds() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                val receipt: Receipt = mockk(relaxed = true)
                val successResult = JudoApiCallResult.Success(receipt)
                val expectedPaymentResult = JudoPaymentResult.Success(mockk(relaxed = true))

                every { receipt.isSoftDeclined } returns false
                every { receipt.isThreeDSecureTwoRequired } returns false
                every { details.toPaymentRequest(any(), any(), any()) } returns mockk(relaxed = true)
                every { judoApiService.payment(any()) } returns call
                coEvery { call.await() } returns successResult
                every { successResult.toJudoPaymentResult(resources) } returns expectedPaymentResult

                val result = sut.payment(details) { null }

                assertEquals(expectedPaymentResult, result)
            }

        @DisplayName("When API returns Success with null receipt, then result from toJudoPaymentResult is returned")
        @Test
        fun returnResultWhenReceiptIsNull() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                val successResult = JudoApiCallResult.Success<Receipt>(null)
                val expectedResult = JudoPaymentResult.Error(mockk(relaxed = true))

                every { details.toPaymentRequest(any(), any(), any()) } returns mockk(relaxed = true)
                every { judoApiService.payment(any()) } returns call
                coEvery { call.await() } returns successResult
                every { successResult.toJudoPaymentResult(resources) } returns expectedResult

                val result = sut.payment(details) { null }

                assertEquals(expectedResult, result)
            }
    }

    @Nested
    @DisplayName("check")
    inner class CheckTests {
        @DisplayName("check: When createTransaction throws, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenCreateTransactionThrows() =
            runTest {
                every { threeDS2Service.createTransaction(any(), any()) } throws RuntimeException("SDK error")

                val result = sut.check(mockk(relaxed = true)) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }
    }

    @Nested
    @DisplayName("save")
    inner class SaveTests {
        @DisplayName("save: When createTransaction throws, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenCreateTransactionThrows() =
            runTest {
                every { threeDS2Service.createTransaction(any(), any()) } throws RuntimeException("SDK error")

                val result = sut.save(mockk(relaxed = true)) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }
    }

    @Nested
    @DisplayName("preAuth")
    inner class PreAuthTests {
        @DisplayName("preAuth: When createTransaction throws, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenCreateTransactionThrows() =
            runTest {
                every { threeDS2Service.createTransaction(any(), any()) } throws RuntimeException("SDK error")

                val result = sut.preAuth(mockk(relaxed = true)) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }
    }

    @Nested
    @DisplayName("paymentWithToken")
    inner class PaymentWithTokenTests {
        @DisplayName("paymentWithToken: When createTransaction throws, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenCreateTransactionThrows() =
            runTest {
                every { threeDS2Service.createTransaction(any(), any()) } throws RuntimeException("SDK error")

                val result = sut.paymentWithToken(mockk(relaxed = true)) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }

        @DisplayName("paymentWithToken: When API call returns Success, then JudoPaymentResult.Success is returned")
        @Test
        fun returnSuccessWhenApiCallSucceeds() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                val receipt: Receipt = mockk(relaxed = true)
                val successResult = JudoApiCallResult.Success(receipt)
                val expectedPaymentResult = JudoPaymentResult.Success(mockk(relaxed = true))

                every { receipt.isSoftDeclined } returns false
                every { receipt.isThreeDSecureTwoRequired } returns false
                every { details.toTokenRequest(any(), any(), any()) } returns mockk(relaxed = true)
                every { judoApiService.tokenPayment(any()) } returns call
                coEvery { call.await() } returns successResult
                every { successResult.toJudoPaymentResult(resources) } returns expectedPaymentResult

                val result = sut.paymentWithToken(details) { null }

                assertEquals(expectedPaymentResult, result)
            }
    }

    @Nested
    @DisplayName("preAuthWithToken")
    inner class PreAuthWithTokenTests {
        @DisplayName("preAuthWithToken: When createTransaction throws, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenCreateTransactionThrows() =
            runTest {
                every { threeDS2Service.createTransaction(any(), any()) } throws RuntimeException("SDK error")

                val result = sut.preAuthWithToken(mockk(relaxed = true)) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }

        @DisplayName("preAuthWithToken: When API call returns Failure, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenApiCallReturnsFail() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                val failureResult = JudoApiCallResult.Failure()
                val expectedPaymentResult = JudoPaymentResult.Error(mockk(relaxed = true))

                every { details.toPreAuthTokenRequest(any(), any(), any()) } returns mockk(relaxed = true)
                every { judoApiService.preAuthTokenPayment(any()) } returns call
                coEvery { call.await() } returns failureResult
                every { failureResult.toJudoPaymentResult(resources) } returns expectedPaymentResult

                val result = sut.preAuthWithToken(details) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }
    }

    @Nested
    @DisplayName("DS certificate provider")
    inner class DsCertificateProviderTests {
        @DisplayName("setCertificateProvider is called before createTransaction")
        @Test
        fun registersProviderBeforeCreateTransaction() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                every { judoApiService.payment(any()) } returns call
                coEvery { call.await() } returns JudoApiCallResult.Failure()

                sut.payment(details) { null }

                verify { threeDS2Service.setCertificateProvider(any()) }
            }
    }

    @Nested
    @DisplayName("recommendation service integration")
    inner class RecommendationTests {
        @DisplayName("When recommendation returns PREVENT action, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenRecommendationPrevents() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val recommendationCall: Call<RecommendationResponse> = mockk(relaxed = true)
                val response: RecommendationResponse = mockk(relaxed = true)
                val data: RecommendationData = mockk(relaxed = true)

                every { recommendationService.isRecommendationFeatureAvailable(any()) } returns true
                every { recommendationService.fetchOptimizationData(any(), any()) } returns recommendationCall
                coEvery { recommendationCall.await() } returns response
                every { response.isValid } returns true
                every { response.data } returns data
                every { data.action } returns RecommendationAction.PREVENT

                val result = sut.payment(details) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }

        @DisplayName("When recommendation fetch throws and shouldHaltTransaction is true, then JudoPaymentResult.Error is returned")
        @Test
        fun returnErrorWhenRecommendationThrowsAndShouldHalt() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val config: RecommendationConfiguration =
                    mockk(relaxed = true) {
                        every { shouldHaltTransactionInCaseOfAnyError } returns true
                    }

                every { recommendationService.isRecommendationFeatureAvailable(any()) } returns true
                every { recommendationService.fetchOptimizationData(any(), any()) } throws RuntimeException("network error")
                every { judo.recommendationConfiguration } returns config

                val result = sut.payment(details) { null }

                assertTrue(result is JudoPaymentResult.Error)
            }

        @DisplayName("When recommendation response is invalid and shouldHalt is false, then API call is attempted")
        @Test
        fun continueWithApiCallWhenRecommendationInvalidAndShouldNotHalt() =
            runTest {
                val details: TransactionDetails = mockk(relaxed = true)
                val recommendationCall: Call<RecommendationResponse> = mockk(relaxed = true)
                val response: RecommendationResponse = mockk(relaxed = true)
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                val receipt: Receipt = mockk(relaxed = true)
                val successResult = JudoApiCallResult.Success(receipt)
                val expectedPaymentResult = JudoPaymentResult.Success(mockk(relaxed = true))

                every { recommendationService.isRecommendationFeatureAvailable(any()) } returns true
                every { recommendationService.fetchOptimizationData(any(), any()) } returns recommendationCall
                coEvery { recommendationCall.await() } returns response
                every { response.isValid } returns false
                every { judo.recommendationConfiguration } returns
                    mockk(relaxed = true) {
                        every { shouldHaltTransactionInCaseOfAnyError } returns false
                    }
                every { receipt.isSoftDeclined } returns false
                every { receipt.isThreeDSecureTwoRequired } returns false
                every { details.toPaymentRequest(any(), any(), any()) } returns mockk(relaxed = true)
                every { judoApiService.payment(any()) } returns call
                coEvery { call.await() } returns successResult
                every { successResult.toJudoPaymentResult(resources) } returns expectedPaymentResult

                val result = sut.payment(details) { null }

                assertEquals(expectedPaymentResult, result)
            }
    }

    @Nested
    @DisplayName("3DS2 challenge")
    inner class ThreeDSecureTwoTests {
        private val details: TransactionDetails = mockk(relaxed = true)
        private val receipt: Receipt = mockk(relaxed = true)
        private val challengeParameters: ChallengeParameters = mockk(relaxed = true)
        private val authParameters: AuthenticationRequestParameters = mockk(relaxed = true)
        private val challengeExtra: ChallengeExtra = mockk(relaxed = true)
        private val completeCall: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
        private val completeResult = JudoApiCallResult.Success(mockk<Receipt>(relaxed = true))
        private val expectedPaymentResult = JudoPaymentResult.Success(mockk(relaxed = true))

        @BeforeEach
        fun setUpChallenge() {
            mockkStatic("com.judopay.judokit.android.api.model.response.ReceiptKt")
            mockkStatic("com.judopay.judo3ds2.ui.challenge.model.ChallengeExtraKt")

            val paymentCall: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
            every { details.toPaymentRequest(any(), any(), any()) } returns mockk(relaxed = true)
            every { details.securityNumber } returns "123"
            every { judoApiService.payment(any()) } returns paymentCall
            coEvery { paymentCall.await() } returns JudoApiCallResult.Success(receipt)
            every { receipt.isSoftDeclined } returns false
            every { receipt.isThreeDSecureTwoRequired } returns true
            every { receipt.receiptId } returns "receipt-1"
            every { receipt.getChallengeParameters(any()) } returns challengeParameters
            every { receipt.getCReqParameters() } returns mockk { every { messageVersion } returns "2.2.0" }
            every { transaction.getAuthenticationRequestParameters() } returns authParameters
            every { authParameters.toChallengeExtra(challengeParameters, THREE_DS_TWO_MIN_TIMEOUT, any()) } returns challengeExtra
            every { judoApiService.complete3ds2(any(), any()) } returns completeCall
            coEvery { completeCall.await() } returns completeResult
            every { completeResult.toJudoPaymentResult(resources) } returns expectedPaymentResult
        }

        @DisplayName("When 3DS2 is required, then the runner gets the challenge extra, receipt ID and message version")
        @Test
        fun runnerReceivesChallengeData() =
            runTest {
                var received: ChallengeData? = null

                sut.payment(details) {
                    received = it
                    "Completed"
                }

                val data = checkNotNull(received)
                assertEquals(transaction, data.transaction)
                assertEquals(challengeParameters, data.challengeParameters)
                assertEquals("receipt-1", data.receiptId)
                assertEquals("2.2.0", data.messageVersion)
                assertEquals(challengeExtra, data.toChallengeExtra())
            }

        @DisplayName("When the challenge cannot be built, then complete3ds2 is still called with a RuntimeError status")
        @Test
        fun completesWhenChallengeExtraCannotBeBuilt() =
            runTest {
                val request = slot<Complete3DS2Request>()
                every { judoApiService.complete3ds2("receipt-1", capture(request)) } returns completeCall
                every {
                    authParameters.toChallengeExtra(any(), any(), any())
                } throws IllegalArgumentException("Missing threeDSServerTransID")

                val result = sut.payment(details) { it.toChallengeExtra().let { "Completed" } }

                assertEquals(expectedPaymentResult, result)
                assertEquals(
                    Complete3DS2Request("2.2.0", "123", runtimeErrorChallengeStatus("Missing threeDSServerTransID")),
                    request.captured,
                )
            }

        @DisplayName("When the challenge runner throws, then complete3ds2 is still called with a RuntimeError status")
        @Test
        fun completesWhenRunnerThrows() =
            runTest {
                val request = slot<Complete3DS2Request>()
                every { judoApiService.complete3ds2("receipt-1", capture(request)) } returns completeCall

                val result = sut.payment(details) { throw IllegalStateException("Launcher not registered") }

                assertEquals(expectedPaymentResult, result)
                assertEquals(Complete3DS2Request("2.2.0", "123", runtimeErrorChallengeStatus("Launcher not registered")), request.captured)
            }

        @DisplayName("When the challenge ends, then complete3ds2 is called with the CV2 and challenge status")
        @Test
        fun completesWithChallengeStatus() =
            runTest {
                val request = slot<Complete3DS2Request>()
                every { judoApiService.complete3ds2("receipt-1", capture(request)) } returns completeCall

                val result = sut.payment(details) { "Completed|SDKTransactionID=1" }

                assertEquals(expectedPaymentResult, result)
                assertEquals(Complete3DS2Request("2.2.0", "123", "Completed|SDKTransactionID=1"), request.captured)
            }
    }

    @Nested
    @DisplayName("complete3ds2")
    inner class Complete3ds2Tests {
        @DisplayName("When called without CV2, then the request carries a null CV2")
        @Test
        fun sendsNullCv2() =
            runTest {
                val call: Call<JudoApiCallResult<Receipt>> = mockk(relaxed = true)
                val apiResult = JudoApiCallResult.Success(mockk<Receipt>(relaxed = true))
                val expected = JudoPaymentResult.Success(mockk(relaxed = true))
                every { judoApiService.complete3ds2("receipt-1", Complete3DS2Request("2.2.0", null, "Cancelled")) } returns call
                coEvery { call.await() } returns apiResult
                every { apiResult.toJudoPaymentResult(resources) } returns expected

                assertEquals(expected, sut.complete3ds2("receipt-1", "2.2.0", null, "Cancelled"))
            }

        @DisplayName("When the API call throws, then JudoPaymentResult.Error is returned")
        @Test
        fun returnsErrorWhenApiThrows() =
            runTest {
                every { judoApiService.complete3ds2(any(), any()) } throws RuntimeException("network")

                assertTrue(sut.complete3ds2("receipt-1", "2.2.0", null, "Cancelled") is JudoPaymentResult.Error)
            }
    }
}
