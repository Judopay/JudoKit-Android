package com.judopay.judokit.android.model

import android.os.Bundle
import android.os.Parcelable
import com.judopay.judokit.android.requireNotNullOrEmpty
import com.judopay.judokit.android.trimIndent
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 *  the Reference object is supposed to simplify storing reference data like consumer, payment
 *  references and metadata dictionary that can hold an arbitrary set of key value based information.
 */
@Parcelize
class Reference internal constructor(
    val consumerReference: String,
    val paymentReference: String,
    val metaData: Bundle? = null,
) : Parcelable {
    /**
     * Builder class for creating an instance of [Reference].
     */
    class Builder {
        private var consumerReference: String? = null
        private var paymentReference: String? = null
        private var metaData: Bundle? = null

        /**
         * Sets your reference for this consumer.
         */
        fun setConsumerReference(reference: String?) = apply { this.consumerReference = reference }

        /**
         * Sets your reference for this payment.
         * If no payment reference is specified then it is set to a randomly generated UUID.
         */
        fun setPaymentReference(reference: String?) = apply { this.paymentReference = reference }

        /**
         * An object containing any additional data you wish to tag this payment with.
         */
        fun setMetaData(data: Bundle?) = apply { this.metaData = data }

        /**
         * Creates an instance of [Reference] based on provided data in setters.
         * @throws IllegalArgumentException If consumerReference is null or empty
         * @return An instance of [Reference]
         */
        fun build(): Reference {
            val message =
                """
                The consumer reference entered is invalid.
                The consumer reference parameter has either not been set or has an incorrect format.
                """.trimIndent(true)

            val myConsumerReference =
                requireNotNullOrEmpty(
                    consumerReference,
                    "consumerReference",
                    message,
                )
            val myPaymentReference =
                if (paymentReference.isNullOrEmpty()) {
                    UUID
                        .randomUUID()
                        .toString()
                } else {
                    paymentReference!!
                }

            return Reference(myConsumerReference, myPaymentReference, metaData)
        }
    }

    override fun toString(): String =
        "Reference(consumerReference='$consumerReference', paymentReference='$paymentReference', metaData=$metaData)"
}
