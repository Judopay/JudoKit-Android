@file:Suppress("MatchingDeclarationName")

package com.judopay.judokit.android.service

import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent

internal object ThreeDSSDKChallengeStatus {
    const val CANCELLED = "Cancelled"
    const val TIMEOUT = "Timeout"
    const val COMPLETED = "Completed"
    const val PROTOCOL_ERROR = "ProtocolError"
    const val RUNTIME_ERROR = "RuntimeError"
}

private fun buildEventString(
    eventType: String,
    vararg pairs: Pair<String, Any?>,
): String =
    buildString {
        append(eventType)
        pairs.forEach { (key, value) ->
            append("|$key=$value")
        }
    }

internal fun CompletionEvent.toFormattedEventString(): String =
    buildEventString(
        ThreeDSSDKChallengeStatus.COMPLETED,
        "SDKTransactionID" to SDKTransactionID,
        "transactionStatus" to transactionStatus,
    )

internal fun ProtocolErrorEvent.toFormattedEventString(): String =
    buildEventString(
        ThreeDSSDKChallengeStatus.PROTOCOL_ERROR,
        "SDKTransactionID" to sdkTransactionID,
        "errorMessage" to
            buildString {
                append("{")
                with(errorMessage) {
                    append(
                        buildEventString(
                            "",
                            "errorCode" to errorCode,
                            "errorComponent" to errorComponent,
                            "errorDescription" to errorDescription,
                            "errorDetails" to errorDetails,
                            "errorMessageType" to errorMessageType,
                            "messageVersionNumber" to messageVersionNumber,
                        ).trimStart('|'),
                    )
                }
                append("}")
            },
    )

internal fun RuntimeErrorEvent.toFormattedEventString(): String =
    buildEventString(
        ThreeDSSDKChallengeStatus.RUNTIME_ERROR,
        "errorCode" to errorCode,
        "errorMessage" to errorMessage,
    )
