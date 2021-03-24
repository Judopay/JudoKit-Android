package com.judopay.judokit.android.api.model.request

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing Ideal sale request builder")
internal class IdealSaleRequestTest {

    private val request = IdealSaleRequest.Builder()

    @Test
    @DisplayName("Given all mandatory fields provided, then build() should return IdealSaleRequest object")
    fun buildSaleRequest() {
        assertEquals(
            request.setAmount("1")
                .setMerchantConsumerReference("reference")
                .setMerchantPaymentReference("reference")
                .setJudoId("judo id")
                .setBic("bic")
                .build(),
            getIdealSaleRequest()
        )
    }

    @Test
    @DisplayName("Given null amount is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullAmount() {
        assertThrows<IllegalArgumentException> {
            request.setAmount(null).build()
        }
    }

    @Test
    @DisplayName("Given null merchantConsumerReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullConsumerReference() {
        assertThrows<IllegalArgumentException> {
            request.setMerchantConsumerReference(null).build()
        }
    }

    @Test
    @DisplayName("Given empty merchantConsumerReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyConsumerReference() {
        assertThrows<IllegalArgumentException> { request.setMerchantConsumerReference("").build() }
    }

    @Test
    @DisplayName("Given null merchantPaymentReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullPaymentReference() {
        assertThrows<IllegalArgumentException> { request.setMerchantPaymentReference(null).build() }
    }

    @Test
    @DisplayName("Given empty merchantPaymentReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyPaymentReference() {
        assertThrows<IllegalArgumentException> { request.setMerchantPaymentReference("").build() }
    }

    @Test
    @DisplayName("Given null judoId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullJudoId() {
        assertThrows<IllegalArgumentException> {
            request.setJudoId(null).build()
        }
    }

    @Test
    @DisplayName("Given empty judoId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyJudoId() {
        assertThrows<IllegalArgumentException> {
            request.setJudoId("").build()
        }
    }

    @Test
    @DisplayName("Given null bic is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullBic() {
        assertThrows<IllegalArgumentException> {
            request.setBic(null).build()
        }
    }

    @Test
    @DisplayName("Given empty bic is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyBic() {
        assertThrows<IllegalArgumentException> {
            request.setBic("").build()
        }
    }

    private fun getIdealSaleRequest() = request.setAmount("1")
        .setMerchantConsumerReference("reference")
        .setMerchantPaymentReference("reference")
        .setJudoId("judo id")
        .setBic("bic").build()
}
