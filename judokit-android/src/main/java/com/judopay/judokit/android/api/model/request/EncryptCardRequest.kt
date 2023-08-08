package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.model.PaymentMethodRavelinModel

/**
 * Represents the data needed to perform the encryption card transaction with the judo API.
 * Use the [EncryptCardRequest.Builder] for object construction.
 *
 *
 * When creating a [EncryptCardRequest] the [EncryptCardRequest.judoId]
 * must be provided.
 */
class EncryptCardRequest private constructor(
    private var paymentMethod: PaymentMethodRavelinModel?
) {
    class Builder {
        private var paymentMethod: PaymentMethodRavelinModel? = null

        fun setPaymentMethod(paymentMethod: PaymentMethodRavelinModel?) = apply { this.paymentMethod = paymentMethod }

        fun build(): EncryptCardRequest {
            val myPaymentMethod = requireNotNull(paymentMethod)
            return EncryptCardRequest(myPaymentMethod)
        }
    }
}
