package com.judopay.judokit.android.api.model.response

import android.content.res.Resources
import com.judopay.judokit.android.model.JudoPaymentResult
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing JudoApiCallResult extension functions")
internal class JudoApiCallResultKtTest {
    private val resources: Resources = mockk(relaxed = true)

    @DisplayName("Given toJudoPaymentResult is called, when JudoApiCallResult is success, then return JudoPaymentResult.Success")
    @Test
    fun returnSuccessOnToJudoPaymentResultCallWithJudoApiCallResultSuccess() {
        assertTrue(
            JudoApiCallResult
                .Success<Receipt>(mockk(relaxed = true))
                .toJudoPaymentResult(resources) is JudoPaymentResult.Success,
        )
    }

    @DisplayName(
        "Given toJudoPaymentResult is called, when JudoApiCallResult is success and data null, then return JudoPaymentResult.Error",
    )
    @Test
    fun returnErrorOnToJudoPaymentResultCallWithJudoApiCallResultSuccessAndDataNull() {
        assertTrue(
            JudoApiCallResult
                .Success<Receipt>(null)
                .toJudoPaymentResult(resources) is JudoPaymentResult.Error,
        )
    }

    @DisplayName("Given toJudoPaymentResult is called, when JudoApiCallResult is error, then return JudoPaymentResult.Error")
    @Test
    fun returnErrorOnToJudoPaymentResultCallWithJudoApiCallResultSuccess() {
        assertTrue(
            JudoApiCallResult
                .Failure()
                .toJudoPaymentResult(resources) is JudoPaymentResult.Error,
        )
    }
}
