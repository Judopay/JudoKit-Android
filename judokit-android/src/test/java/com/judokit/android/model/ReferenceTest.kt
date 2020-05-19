package com.judokit.android.model

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Test Reference builder")
internal class ReferenceTest {
    private val sut = Reference.Builder()
        .setConsumerReference("consumerRef")
        .setPaymentReference("PaymentRef")
        .setMetaData(mockk())

    @DisplayName("Given build is called, when consumerReference is null, then throw exception")
    @Test
    fun throwExceptionOnConsumerReferenceNull() {
        assertThrows<IllegalArgumentException> { sut.setConsumerReference(null).build() }
    }

    @DisplayName("Given build is called, when consumerReference is empty, then throw exception")
    @Test
    fun throwExceptionOnConsumerReferenceEmpty() {
        assertThrows<IllegalArgumentException> { sut.setConsumerReference("").build() }
    }

    @DisplayName("Given build is called, when paymentReference is null, then no exception is thrown")
    @Test
    fun noExceptionThrownOnPaymentReferenceNull() {
        assertDoesNotThrow { sut.setPaymentReference(null).build() }
    }

    @DisplayName("Given build is called, when paymentReference is empty, then no exception is thrown")
    @Test
    fun noExceptionThrownOnPaymentReferenceEmpty() {
        assertDoesNotThrow { sut.setPaymentReference("").build() }
    }

    @DisplayName("Given build is called, when all require fields are valid, then no exception is thrown")
    @Test
    fun noExceptionThrownOnAllFieldsValid() {
        assertDoesNotThrow { sut.build() }
    }
}