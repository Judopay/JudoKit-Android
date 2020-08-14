package com.judokit.android.api.model.request

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

@DisplayName("Testing Bank sale request builder")
internal class BankSaleRequestTest {

    private val sut = BankSaleRequest.Builder()

    @Test
    @DisplayName("Given all mandatory fields provided, then build() should return BankSaleRequest object")
    fun buildSaleRequest() {
        assertEquals(
            sut.setAmount(BigDecimal(1))
                .setMerchantConsumerReference("reference")
                .setMerchantPaymentReference("reference")
                .setJudoId("judo id")
                .setMerchantRedirectUrl("judo://pay")
                .build(),
            getBankSaleRequest()
        )
    }

    @Test
    @DisplayName("Given null amount is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullAmount() {
        assertThrows<IllegalArgumentException> {
            sut.setAmount(null).build()
        }
    }

    @Test
    @DisplayName("Given null merchantConsumerReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullConsumerReference() {
        assertThrows<IllegalArgumentException> {
            sut.setMerchantConsumerReference(null).build()
        }
    }

    @Test
    @DisplayName("Given empty merchantConsumerReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyConsumerReference() {
        assertThrows<IllegalArgumentException> {
            sut.setMerchantConsumerReference(
                ""
            ).build()
        }
    }

    @Test
    @DisplayName("Given null merchantPaymentReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullPaymentReference() {
        assertThrows<IllegalArgumentException> {
            sut.setMerchantPaymentReference(
                null
            ).build()
        }
    }

    @Test
    @DisplayName("Given empty merchantPaymentReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyPaymentReference() {
        assertThrows<IllegalArgumentException> {
            sut.setMerchantPaymentReference(
                ""
            ).build()
        }
    }

    @Test
    @DisplayName("Given null judoId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullJudoId() {
        assertThrows<IllegalArgumentException> {
            sut.setJudoId(null).build()
        }
    }

    @Test
    @DisplayName("Given empty judoId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyJudoId() {
        assertThrows<IllegalArgumentException> {
            sut.setJudoId("").build()
        }
    }

    private fun getBankSaleRequest() = sut.setAmount(BigDecimal(1))
        .setMerchantConsumerReference("reference")
        .setMerchantPaymentReference("reference")
        .setJudoId("judo id")
        .build()
}
