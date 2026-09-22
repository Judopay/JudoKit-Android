package com.judopay.judokit.android.api.model.response.cdn

import com.google.gson.annotations.SerializedName

internal data class DsCertsResponse(
    @SerializedName("schemaVersion") val schemaVersion: String,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("etag") val etag: String,
    @SerializedName("entries") val entries: List<DsCertEntry>,
)

internal fun String.isSupportedSchemaMajor(): Boolean {
    val major = split(".").firstOrNull()?.toIntOrNull() ?: return false
    return major == 1
}
