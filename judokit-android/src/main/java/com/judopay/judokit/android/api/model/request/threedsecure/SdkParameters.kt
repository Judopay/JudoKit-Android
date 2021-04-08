package com.judopay.judokit.android.api.model.request.threedsecure

import com.google.gson.annotations.SerializedName
import com.judopay.judokit.android.requireNotNull
import com.judopay.judokit.android.requireNotNullOrEmpty

class SdkParameters private constructor(
    private val applicationId: String,
    private val encodedData: String,
    private val maxTimeout: Int,
    private val referenceNumber: String,
    private val transactionId: String,
    private val deviceRenderOptions: DeviceRenderOptions,
    @SerializedName("ephemeral_public_key")
    private val ephemeralPublicKey: EphemeralPublicKey,
) {
    class Builder {
        private var applicationId: String? = null
        private var encodedData: String? = null
        private var ephemeralPublicKey: EphemeralPublicKey? = null
        private var maxTimeout: Int? = null
        private var referenceNumber: String? = null
        private var transactionId: String? = null
        private var deviceRenderOptions: DeviceRenderOptions? = null

        fun setApplicationId(applicationId: String?) =
            apply { this.applicationId = applicationId }

        fun setEncodedData(encodedData: String?) = apply { this.encodedData = encodedData }

        fun setEphemeralPublicKey(ephemeralPublicKey: EphemeralPublicKey?) =
            apply { this.ephemeralPublicKey = ephemeralPublicKey }

        fun setMaxTimeout(maxTimeout: Int?) = apply { this.maxTimeout = maxTimeout }

        fun setReferenceNumber(referenceNumber: String?) =
            apply { this.referenceNumber = referenceNumber }

        fun setTransactionId(transactionId: String?) = apply { this.transactionId = transactionId }

        fun setDeviceRenderOptions(deviceRenderOptions: DeviceRenderOptions?) =
            apply { this.deviceRenderOptions = deviceRenderOptions }

        fun build(): SdkParameters {
            val myApplicationId = requireNotNullOrEmpty(applicationId, "applicationId")
            val myEncodedData = requireNotNullOrEmpty(encodedData, "encodedData")
            val myEphemeralPublicKey = requireNotNull(ephemeralPublicKey, "ephemeralPublicKey")
            val myMaxTimeout = requireNotNull(maxTimeout, "maxTimeout")
            val myReferenceNumber = requireNotNullOrEmpty(referenceNumber, "referenceNumber")
            val myTransactionId = requireNotNullOrEmpty(transactionId, "transactionId")
            val myDeviceRenderOptions = deviceRenderOptions ?: DeviceRenderOptions()

            return SdkParameters(
                myApplicationId,
                myEncodedData,
                myMaxTimeout,
                myReferenceNumber,
                myTransactionId,
                myDeviceRenderOptions,
                myEphemeralPublicKey
            )
        }
    }
}
