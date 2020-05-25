package com.judokit.android.api.model.request

import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

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
                .setSiteId("site id")
                .build(),
            getBankSaleRequest()
        )
    }

    @Test
    @DisplayName("Given null amount is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullAmount() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.setAmount(null).build()
        }
    }

    @Test
    @DisplayName("Given null merchantConsumerReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullConsumerReference() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.setMerchantConsumerReference(null).build()
        }
    }

    @Test
    @DisplayName("Given empty merchantConsumerReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyConsumerReference() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.setMerchantConsumerReference(
                ""
            ).build()
        }
    }

    @Test
    @DisplayName("Given null merchantPaymentReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullPaymentReference() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.setMerchantPaymentReference(
                null
            ).build()
        }
    }

    @Test
    @DisplayName("Given empty merchantPaymentReference is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptyPaymentReference() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.setMerchantPaymentReference(
                ""
            ).build()
        }
    }

    @Test
    @DisplayName("Given null siteId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullSiteId() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.setSiteId(null).build()
        }
    }

    @Test
    @DisplayName("Given empty siteId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptySiteId() {
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.setSiteId("").build()
        }
    }

    private fun getBankSaleRequest() = sut.setAmount(BigDecimal(1))
        .setMerchantConsumerReference("reference")
        .setMerchantPaymentReference("reference")
        .setSiteId("site id")
        .build()
}
