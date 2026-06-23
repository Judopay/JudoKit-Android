package com.judopay.judokit.android.api.model.response.cdn

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal data class DsCertEntry(
    @SerializedName("dsId") val dsId: String,
    @SerializedName("dsName") val dsName: String,
    @SerializedName("dsCertificate") val dsCertificate: String,
    @SerializedName("rootCertificates") val rootCertificates: List<String> = emptyList(),
    @SerializedName("keyId") val keyId: String,
    @SerializedName("validUntil") val validUntil: String? = null,
)

/**
 * Parses [DsCertEntry.validUntil] (ISO-8601 UTC) into epoch millis, or `null` when it is absent
 * or unparseable. Callers decide how to treat `null` (see [isNotExpired] / [isNearExpiry]).
 */
private fun DsCertEntry.validUntilEpochMillis(): Long? {
    val until = validUntil ?: return null
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .parse(until)
            ?.time
    }.getOrNull()
}

internal fun DsCertEntry.isNotExpired(now: Long): Boolean {
    val epochMillis = validUntilEpochMillis()
    return validUntil == null || (epochMillis != null && epochMillis > now)
}

internal fun DsCertEntry.isNearExpiry(
    now: Long,
    thresholdMs: Long,
): Boolean {
    val epochMillis = validUntilEpochMillis() ?: return false
    return (epochMillis - now) < thresholdMs
}
