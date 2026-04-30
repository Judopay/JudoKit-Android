package com.judopay.judokit.android.service

import android.content.Context
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import com.judopay.judo3ds2.service.ThreeDS2Service
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
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
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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

        sut = CardTransactionRepository(judo, judoApiService, threeDS2Service, recommendationService, resources, context)
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

                val result = sut.payment(mockk(relaxed = true)) { _, _ -> null }

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

                val result = sut.payment(details) { _, _ -> null }

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

                val result = sut.payment(details) { _, _ -> null }

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

                val result = sut.payment(details) { _, _ -> null }

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

                val result = sut.check(mockk(relaxed = true)) { _, _ -> null }

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

                val result = sut.save(mockk(relaxed = true)) { _, _ -> null }

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

                val result = sut.preAuth(mockk(relaxed = true)) { _, _ -> null }

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

                val result = sut.paymentWithToken(mockk(relaxed = true)) { _, _ -> null }

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

                val result = sut.paymentWithToken(details) { _, _ -> null }

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

                val result = sut.preAuthWithToken(mockk(relaxed = true)) { _, _ -> null }

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

                val result = sut.preAuthWithToken(details) { _, _ -> null }

                assertTrue(result is JudoPaymentResult.Error)
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

                val result = sut.payment(details) { _, _ -> null }

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

                val result = sut.payment(details) { _, _ -> null }

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
                every { judo.recommendationConfiguration } returns mockk(relaxed = true) {
                    every { shouldHaltTransactionInCaseOfAnyError } returns false
                }
                every { receipt.isSoftDeclined } returns false
                every { receipt.isThreeDSecureTwoRequired } returns false
                every { details.toPaymentRequest(any(), any(), any()) } returns mockk(relaxed = true)
                every { judoApiService.payment(any()) } returns call
                coEvery { call.await() } returns successResult
                every { successResult.toJudoPaymentResult(resources) } returns expectedPaymentResult

                val result = sut.payment(details) { _, _ -> null }

                assertEquals(expectedPaymentResult, result)
            }
    }
}
