package com.judopay.judokit.android.api.model.response.cdn

internal data class DsCertsCache(
    val etag: String,
    val lastModified: String,
    val fetchedAt: Long,
    val maxAgeMs: Long,
    val entries: List<DsCertEntry>,
)

internal fun DsCertsCache.isFresh(now: Long): Boolean = (now - fetchedAt) < maxAgeMs

internal fun DsCertsCache.hasNearExpiryEntry(
    now: Long,
    thresholdMs: Long,
): Boolean = entries.any { it.isNearExpiry(now, thresholdMs) }
