package com.judopay.judokit.android.service

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.model.GooglePayConfiguration
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing JudoGooglePayService")
internal class JudoGooglePayServiceTest {
    private val paymentsClient: PaymentsClient = mockk(relaxed = true)
    private val judo: Judo = mockk(relaxed = true)
    private val launcher: ActivityResultLauncher<IntentSenderRequest> = mockk(relaxed = true)
    private val googlePayConfiguration: GooglePayConfiguration = mockk(relaxed = true)

    private lateinit var sut: JudoGooglePayService

    @BeforeEach
    fun setUp() {
        every { judo.googlePayConfiguration } returns googlePayConfiguration
        sut = JudoGooglePayService(paymentsClient, judo, launcher)
    }

    @Nested
    @DisplayName("loadGooglePayPaymentData")
    inner class LoadGooglePayPaymentDataTests {
        @DisplayName("When loadPaymentData succeeds, then onSuccess callback is invoked with PaymentData")
        @Test
        fun invokeOnSuccessWhenLoadPaymentDataSucceeds() {
            val paymentData: PaymentData = mockk(relaxed = true)
            val task: Task<PaymentData> = mockk(relaxed = true)

            every { paymentsClient.loadPaymentData(any()) } returns task
            every { task.addOnSuccessListener(any()) } answers {
                firstArg<OnSuccessListener<PaymentData>>().onSuccess(paymentData)
                task
            }

            var receivedPaymentData: PaymentData? = null
            sut.loadGooglePayPaymentData(
                onSuccess = { receivedPaymentData = it },
                onError = { },
            )

            assertTrue(receivedPaymentData === paymentData)
        }

        @DisplayName("When loadPaymentData fails with ResolvableApiException, then launcher.launch is called")
        @Test
        fun launchWhenLoadPaymentDataFailsWithResolvableApiException() {
            val task: Task<PaymentData> = mockk(relaxed = true)
            val resolvableException: ResolvableApiException = mockk(relaxed = true)

            every { paymentsClient.loadPaymentData(any()) } returns task
            every { task.addOnSuccessListener(any()) } returns task
            every { task.addOnFailureListener(any()) } answers {
                firstArg<OnFailureListener>().onFailure(resolvableException)
                task
            }

            sut.loadGooglePayPaymentData(
                onSuccess = { },
                onError = { },
            )

            verify { launcher.launch(any()) }
        }

        @DisplayName("When loadPaymentData fails with non-resolvable exception, then onError callback is invoked")
        @Test
        fun invokeOnErrorWhenLoadPaymentDataFailsWithNonResolvableException() {
            val task: Task<PaymentData> = mockk(relaxed = true)
            val exception = ApiException(Status.RESULT_INTERNAL_ERROR)

            every { paymentsClient.loadPaymentData(any()) } returns task
            every { task.addOnSuccessListener(any()) } returns task
            every { task.addOnFailureListener(any()) } answers {
                firstArg<OnFailureListener>().onFailure(exception)
                task
            }

            var errorMessage: String? = null
            sut.loadGooglePayPaymentData(
                onSuccess = { },
                onError = { errorMessage = it },
            )

            assertTrue(errorMessage != null)
        }

        @DisplayName("When googlePayConfiguration is null, then paymentsClient.loadPaymentData is not called")
        @Test
        fun doesNotCallLoadPaymentDataWhenGooglePayConfigurationIsNull() {
            every { judo.googlePayConfiguration } returns null
            val sutWithNullConfig = JudoGooglePayService(paymentsClient, judo, launcher)

            sutWithNullConfig.loadGooglePayPaymentData(
                onSuccess = { },
                onError = { },
            )

            verify(exactly = 0) { paymentsClient.loadPaymentData(any()) }
        }
    }

    @Nested
    @DisplayName("checkIfGooglePayIsAvailable")
    inner class CheckIfGooglePayIsAvailableTests {
        @ExperimentalCoroutinesApi
        @DisplayName("When isReadyToPay task is successful, then return true")
        @Test
        fun returnTrueWhenIsReadyToPayIsSuccessful() =
            runTest {
                val task: Task<Boolean> = mockk(relaxed = true)
                every { paymentsClient.isReadyToPay(any()) } returns task
                every { task.isSuccessful } returns true
                every { task.exception } returns null
                every { task.addOnCompleteListener(any()) } answers {
                    firstArg<com.google.android.gms.tasks.OnCompleteListener<Boolean>>().onComplete(task)
                    task
                }

                val result = sut.checkIfGooglePayIsAvailable()

                assertTrue(result)
            }

        @ExperimentalCoroutinesApi
        @DisplayName("When isReadyToPay task is not successful, then return false")
        @Test
        fun returnFalseWhenIsReadyToPayIsNotSuccessful() =
            runTest {
                val task: Task<Boolean> = mockk(relaxed = true)
                every { paymentsClient.isReadyToPay(any()) } returns task
                every { task.isSuccessful } returns false
                every { task.exception } returns null
                every { task.addOnCompleteListener(any()) } answers {
                    firstArg<com.google.android.gms.tasks.OnCompleteListener<Boolean>>().onComplete(task)
                    task
                }

                val result = sut.checkIfGooglePayIsAvailable()

                assertFalse(result)
            }

        @ExperimentalCoroutinesApi
        @DisplayName("When isReadyToPay task throws ApiException, then propagate the exception")
        @Test
        fun propagateApiExceptionFromIsReadyToPay() {
            val task: Task<Boolean> = mockk(relaxed = true)
            val exception = ApiException(Status.RESULT_INTERNAL_ERROR)
            every { paymentsClient.isReadyToPay(any()) } returns task
            every { task.isSuccessful } returns false
            every { task.exception } returns exception
            every { task.addOnCompleteListener(any()) } answers {
                firstArg<com.google.android.gms.tasks.OnCompleteListener<Boolean>>().onComplete(task)
                task
            }

            assertThrows<ApiException> {
                runTest {
                    sut.checkIfGooglePayIsAvailable()
                }
            }
        }

        @ExperimentalCoroutinesApi
        @DisplayName("When googlePayConfiguration is null, then throw IllegalStateException")
        @Test
        fun throwIllegalStateExceptionWhenGooglePayConfigurationIsNull() {
            every { judo.googlePayConfiguration } returns null
            val sutWithNullConfig = JudoGooglePayService(paymentsClient, judo, launcher)

            assertThrows<IllegalStateException> {
                runTest {
                    sutWithNullConfig.checkIfGooglePayIsAvailable()
                }
            }
        }
    }
}
