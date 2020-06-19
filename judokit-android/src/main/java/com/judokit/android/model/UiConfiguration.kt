package com.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UiConfiguration internal constructor(
    val avsEnabled: Boolean,
    val shouldPaymentMethodsDisplayAmount: Boolean,
    val shouldPaymentButtonDisplayAmount: Boolean,
    val shouldPaymentMethodsVerifySecurityCode: Boolean
) : Parcelable {

    class Builder {
        private var avsEnabled: Boolean? = null
        private var shouldPaymentMethodsDisplayAmount: Boolean? = null
        private var shouldPaymentMethodsVerifySecurityCode: Boolean? = null
        private var shouldPaymentButtonDisplayAmount: Boolean? = null

        fun setAvsEnabled(enabled: Boolean?) = apply { this.avsEnabled = enabled }

        fun setShouldPaymentMethodsDisplayAmount(shouldPaymentMethodsDisplayAmount: Boolean?) =
            apply { this.shouldPaymentMethodsDisplayAmount = shouldPaymentMethodsDisplayAmount }

        fun setShouldPaymentMethodsVerifySecurityCode(shouldPaymentMethodsVerifySecurityCode: Boolean?) =
            apply {
                this.shouldPaymentMethodsVerifySecurityCode = shouldPaymentMethodsVerifySecurityCode
            }

        fun setShouldPaymentButtonDisplayAmount(shouldPaymentButtonDisplayAmount: Boolean?) =
            apply { this.shouldPaymentButtonDisplayAmount = shouldPaymentButtonDisplayAmount }

        fun build(): UiConfiguration {
            val avsEnabled = this.avsEnabled ?: false
            val shouldPaymentMethodsDisplayAmount = this.shouldPaymentMethodsDisplayAmount ?: true
            val shouldPaymentMethodsVerifySecurityCode =
                this.shouldPaymentMethodsVerifySecurityCode ?: true
            val shouldPaymentButtonDisplayAmount = this.shouldPaymentButtonDisplayAmount ?: false

            return UiConfiguration(
                avsEnabled,
                shouldPaymentMethodsDisplayAmount,
                shouldPaymentButtonDisplayAmount,
                shouldPaymentMethodsVerifySecurityCode
            )
        }
    }

    override fun toString(): String {
        return "UiConfiguration(avsEnabled=$avsEnabled, shouldDisplayAmount=$shouldPaymentMethodsDisplayAmount, shouldEnterSecurityCode=$shouldPaymentMethodsVerifySecurityCode)"
    }
}
