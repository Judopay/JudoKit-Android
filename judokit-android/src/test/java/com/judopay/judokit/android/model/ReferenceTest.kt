package com.judopay.judokit.android.model

import android.os.Bundle
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Test Reference builder")
internal class ReferenceTest {
    private val sut =
        Reference
            .Builder()
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

    @DisplayName("Given build is called with null paymentReference, then paymentReference is generated as UUID")
    @Test
    fun paymentReferenceIsGeneratedAsUUIDWhenNull() {
        val reference = sut.setPaymentReference(null).build()
        assertNotNull(reference.paymentReference)
        assertTrue(reference.paymentReference.isNotEmpty())
    }

    @DisplayName("Given build is called with empty paymentReference, then paymentReference is generated as UUID")
    @Test
    fun paymentReferenceIsGeneratedAsUUIDWhenEmpty() {
        val reference = sut.setPaymentReference("").build()
        assertNotNull(reference.paymentReference)
        assertTrue(reference.paymentReference.isNotEmpty())
    }

    @DisplayName("Given a Reference instance, then toString contains consumerReference and paymentReference")
    @Test
    fun toStringContainsReferences() {
        val reference = sut.build()
        val str = reference.toString()
        assertTrue(str.contains("consumerRef"))
        assertTrue(str.contains("PaymentRef"))
    }

    @DisplayName("Given setMetaData is called, then build includes the metaData")
    @Test
    fun setMetaDataIsRetainedInBuiltReference() {
        val bundle = mockk<Bundle>()
        val reference = sut.setMetaData(bundle).build()
        assertNotNull(reference.metaData)
    }

    @DisplayName("Given valid consumerReference, then reference.consumerReference equals the input")
    @Test
    fun consumerReferenceIsRetainedInBuiltReference() {
        val reference = sut.build()
        assertEquals("consumerRef", reference.consumerReference)
    }

    @DisplayName("Given non-empty paymentReference, then reference.paymentReference equals the provided value")
    @Test
    fun paymentReferenceIsRetainedWhenProvided() {
        val reference = sut.setPaymentReference("myPaymentRef").build()
        assertEquals("myPaymentRef", reference.paymentReference)
    }
}
