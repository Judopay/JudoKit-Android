package com.judopay.api.model.request

import java.math.BigDecimal
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
            request.setAmount(BigDecimal(1))
                .setMerchantConsumerReference("reference")
                .setMerchantPaymentReference("reference")
                .setSiteId("site id")
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
    @DisplayName("Given null siteId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnNullSiteId() {
        assertThrows<IllegalArgumentException> {
            request.setSiteId(null).build()
        }
    }

    @Test
    @DisplayName("Given empty siteId is provided, then build() should throw IllegalArgumentException")
    fun exceptionOnEmptySiteId() {
        assertThrows<IllegalArgumentException> {
            request.setSiteId("").build()
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

    private fun getIdealSaleRequest() = request.setAmount(BigDecimal(1))
        .setMerchantConsumerReference("reference")
        .setMerchantPaymentReference("reference")
        .setSiteId("site id")
        .setBic("bic").build()
}
