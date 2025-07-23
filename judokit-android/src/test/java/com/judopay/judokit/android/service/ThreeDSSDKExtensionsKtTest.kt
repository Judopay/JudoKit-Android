package com.judopay.judokit.android.service

import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.transaction.challenge.ErrorMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThreeDSSDKExtensionsKtTest {
    @Test
    fun `toFormattedEventString should format CompletionEvent correctly`() {
        val completionEvent =
            CompletionEvent(
                SDKTransactionID = "12345",
                transactionStatus = "Y",
            )

        val result = completionEvent.toFormattedEventString()
        val expected = "Completed|SDKTransactionID=12345|transactionStatus=Y"

        assertEquals(expected, result)
    }

    @Test
    fun `toFormattedEventString should handle CompletionEvent with empty fields`() {
        val completionEvent =
            CompletionEvent(
                SDKTransactionID = "",
                transactionStatus = "",
            )

        val result = completionEvent.toFormattedEventString()
        val expected = "Completed|SDKTransactionID=|transactionStatus="

        assertEquals(expected, result)
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `toFormattedEventString should format ProtocolErrorEvent correctly`() {
        val protocolErrorEvent =
            ProtocolErrorEvent(
                sdkTransactionID = "12345",
                errorMessage =
                    ErrorMessage(
                        errorCode = "404",
                        errorComponent = "A",
                        errorDescription = "Not Found",
                        errorDetails = "Details about error",
                        errorMessageType = "MessageType",
                        messageVersionNumber = "2.1.0",
                    ),
            )

        val result = protocolErrorEvent.toFormattedEventString()
        val expected =
            "ProtocolError|SDKTransactionID=12345|errorMessage={errorCode=404|errorComponent=A|errorDescription=Not Found|errorDetails=Details about error|errorMessageType=MessageType|messageVersionNumber=2.1.0}"

        assertEquals(expected, result)
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    fun `toFormattedEventString should handle ProtocolErrorEvent with empty errorMessage fields`() {
        val protocolErrorEvent =
            ProtocolErrorEvent(
                sdkTransactionID = "12345",
                errorMessage =
                    ErrorMessage(
                        errorCode = "",
                        errorComponent = "",
                        errorDescription = "",
                        errorDetails = "",
                        errorMessageType = "",
                        messageVersionNumber = "",
                    ),
            )

        val result = protocolErrorEvent.toFormattedEventString()
        val expected =
            "ProtocolError|SDKTransactionID=12345|errorMessage={errorCode=|errorComponent=|errorDescription=|errorDetails=|errorMessageType=|messageVersionNumber=}"

        assertEquals(expected, result)
    }

    @Test
    fun `toFormattedEventString should format RuntimeErrorEvent correctly`() {
        val runtimeErrorEvent =
            RuntimeErrorEvent(
                errorCode = "500",
                errorMessage = "Internal Server Error",
            )

        val result = runtimeErrorEvent.toFormattedEventString()
        val expected = "RuntimeError|errorCode=500|errorMessage=Internal Server Error"

        assertEquals(expected, result)
    }

    @Test
    fun `toFormattedEventString should handle RuntimeErrorEvent with empty fields`() {
        val runtimeErrorEvent =
            RuntimeErrorEvent(
                errorCode = "",
                errorMessage = "",
            )

        val result = runtimeErrorEvent.toFormattedEventString()
        val expected = "RuntimeError|errorCode=|errorMessage="

        assertEquals(expected, result)
    }
}
