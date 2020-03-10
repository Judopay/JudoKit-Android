package com.judopay.model

import android.os.Bundle
import android.os.Parcelable
import com.judopay.requireNotNullOrEmpty
import kotlinx.android.parcel.Parcelize

@Parcelize
class Reference internal constructor(val consumerReference: String,
                                     val paymentReference: String,
                                     val metaData: Bundle? = null) : Parcelable {


    class Builder {
        private var consumerReference: String? = null
        private var paymentReference: String? = null
        private var metaData: Bundle? = null

        fun setConsumerReference(reference: String?) = apply { this.consumerReference = reference }
        fun setPaymentReference(reference: String?) = apply { this.paymentReference = reference }
        fun setMetaData(data: Bundle?) = apply { this.metaData = data }

        fun build(): Reference {
            val myConsumerReference = requireNotNullOrEmpty(consumerReference, "consumerReference")
            val myPaymentReference = requireNotNullOrEmpty(paymentReference, "paymentReference")

            return Reference(myConsumerReference, myPaymentReference, metaData)
        }
    }

    override fun toString(): String {
        return "Reference(consumerReference='$consumerReference', paymentReference='$paymentReference', metaData=$metaData)"
    }
}
