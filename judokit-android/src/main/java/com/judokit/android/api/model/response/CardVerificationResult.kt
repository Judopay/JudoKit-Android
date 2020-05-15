package com.judokit.android.api.model.response

import com.google.gson.annotations.SerializedName

/**
 * The result from a 3D-Secure verification, containing the data required to complete the
 * transaction with the judo API.
 */
class CardVerificationResult(
    @field:SerializedName("MD") val md: String,
    @field:SerializedName("PaRes") val paRes: String
)
