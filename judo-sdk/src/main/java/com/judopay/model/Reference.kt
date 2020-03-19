package com.judopay.model

import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
class Reference internal constructor(
    val consumerReference: String,
    val paymentReference: String,
    val metaData: Bundle? = null
) : Parcelable {

    class Builder {
        private var consumerReference: String? = null
        private var paymentReference: String? = null
        private var metaData: Bundle? = null

        fun setConsumerReference(reference: String?) = apply { this.consumerReference = reference }
        fun setPaymentReference(reference: String?) = apply { this.paymentReference = reference }
        fun setMetaData(data: Bundle?) = apply { this.metaData = data }

        fun build(): Reference {
            val myConsumerReference = if (consumerReference.isNullOrBlank()) UUID.randomUUID()
                .toString() else consumerReference!!
            val myPaymentReference = if (paymentReference.isNullOrBlank()) UUID.randomUUID()
                .toString() else paymentReference!!

            return Reference(myConsumerReference, myPaymentReference, metaData)
        }
    }

    override fun toString(): String {
        return "Reference(consumerReference='$consumerReference', paymentReference='$paymentReference', metaData=$metaData)"
    }
}
