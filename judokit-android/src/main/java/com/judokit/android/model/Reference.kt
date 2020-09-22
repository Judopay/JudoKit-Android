package com.judokit.android.model

import android.os.Bundle
import android.os.Parcelable
import com.judokit.android.requireNotNullOrEmpty
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
            val myConsumerReference = requireNotNullOrEmpty(
                consumerReference,
                "consumerReference",
                "The consumer reference entered is invalid. The consumer reference parameter has either not been set or has an incorrect format."
            )
            val myPaymentReference = if (paymentReference.isNullOrEmpty()) UUID.randomUUID()
                .toString() else paymentReference!!

            return Reference(myConsumerReference, myPaymentReference, metaData)
        }
    }

    override fun toString(): String {
        return "Reference(consumerReference='$consumerReference', paymentReference='$paymentReference', metaData=$metaData)"
    }
}
