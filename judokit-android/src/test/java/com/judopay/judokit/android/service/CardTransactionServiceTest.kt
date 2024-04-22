package com.judopay.judokit.android.service

import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import retrofit2.await

@DisplayName("Testing cardTransactionService class")
internal class CardTransactionServiceTest {
    private val service: JudoApiService = mockk(relaxed = true)
    private val judoApiCallResult = JudoApiCallResult.Success(mockk<Receipt>(relaxed = true))

    @BeforeEach
    internal fun setUp() {
        mockkStatic("retrofit2.KotlinExtensions")
        mockkStatic("com.judopay.judokit.android.ui.paymentmethods.MappersKt")
        mockkStatic("com.judopay.judokit.android.ui.common.FunctionsKt")

        coEvery {
            service.payment(any()).await().hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.preAuthPayment(any()).await().hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.checkCard(any()).await().hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.saveCard(any()).await().hint(JudoApiCallResult::class)
        } returns judoApiCallResult

        coEvery {
            service.registerCard(any()).await().hint(JudoApiCallResult::class)
        } returns judoApiCallResult
        coEvery {
            service.tokenPayment(any()).await()
        } returns mockk(relaxed = true)
        coEvery {
            service.preAuthTokenPayment(any()).await()
        } returns mockk(relaxed = true)
    }
}
