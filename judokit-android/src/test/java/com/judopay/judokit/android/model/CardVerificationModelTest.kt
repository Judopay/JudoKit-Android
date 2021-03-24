package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing CardVerificationModel builder")
internal class CardVerificationModelTest {

    private val sut = CardVerificationModel.Builder()
        .setAcsUrl("acs")
        .setMd("md")
        .setPaReq("pareq")
        .setReceiptId("id")

    @DisplayName("Given build is called, when receiptId is null, then throw exception")
    @Test
    fun throwExceptionOnReceiptIdNull() {
        assertThrows<IllegalArgumentException> { sut.setReceiptId(null).build() }
    }

    @DisplayName("Given build is called, when receiptId is empty, then throw exception")
    @Test
    fun throwExceptionOnReceiptIdEmpty() {
        assertThrows<IllegalArgumentException> { sut.setReceiptId("").build() }
    }

    @DisplayName("Given build is called, when acsUrl is null, then throw exception")
    @Test
    fun throwExceptionOnAcsUrlNull() {
        assertThrows<IllegalArgumentException> { sut.setAcsUrl(null).build() }
    }

    @DisplayName("Given build is called, when acsUrl is empty, then throw exception")
    @Test
    fun throwExceptionOnAcsUrlEmpty() {
        assertThrows<IllegalArgumentException> { sut.setAcsUrl("").build() }
    }

    @DisplayName("Given build is called, when md is null, then throw exception")
    @Test
    fun throwExceptionOnMdNull() {
        assertThrows<IllegalArgumentException> { sut.setMd(null).build() }
    }

    @DisplayName("Given build is called, when md is empty, then throw exception")
    @Test
    fun throwExceptionOnMdEmpty() {
        assertThrows<IllegalArgumentException> { sut.setMd("").build() }
    }

    @DisplayName("Given build is called, when paReq is null, then throw exception")
    @Test
    fun throwExceptionOnPaReqNull() {
        assertThrows<IllegalArgumentException> { sut.setPaReq(null).build() }
    }

    @DisplayName("Given build is called, when paReq is empty, then throw exception")
    @Test
    fun throwExceptionOnPaReqEmpty() {
        assertThrows<IllegalArgumentException> { sut.setPaReq("").build() }
    }

    @DisplayName("Given build is called, when all required fields are valid, then no exception is thrown")
    @Test
    fun noExceptionIsThrownOnBuildWithRequiredFieldsValid() {
        assertDoesNotThrow {
            sut.build()
        }
    }
}
