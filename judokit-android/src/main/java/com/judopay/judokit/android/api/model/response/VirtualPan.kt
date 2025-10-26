package com.judopay.judokit.android.api.model.response

class VirtualPan(
    val lastFour: String? = null,
    val expiryDate: String? = null,
) {
    override fun toString(): String {
        return "VirtualPan(lastFour=$lastFour, expiryDate=$expiryDate)"
    }
}
