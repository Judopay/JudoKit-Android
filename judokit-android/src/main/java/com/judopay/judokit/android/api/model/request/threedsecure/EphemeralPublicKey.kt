package com.judopay.judokit.android.api.model.request.threedsecure

data class EphemeralPublicKey(
    val kty: String,
    val crv: String,
    val x: String,
    val y: String,
)
