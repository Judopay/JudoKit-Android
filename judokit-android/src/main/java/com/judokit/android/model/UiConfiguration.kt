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
        private var avsEnabled: Boolean? = false
        private var shouldPaymentMethodsDisplayAmount: Boolean? = true
        private var shouldPaymentMethodsVerifySecurityCode: Boolean? = true
        private var shouldPaymentButtonDisplayAmount: Boolean? = false

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
            val avsEnabled = requireNotNull(this.avsEnabled)
            val shouldPaymentMethodsDisplayAmount =
                requireNotNull(this.shouldPaymentMethodsDisplayAmount)
            val shouldPaymentMethodsVerifySecurityCode =
                requireNotNull(this.shouldPaymentMethodsVerifySecurityCode)
            val shouldPaymentButtonDisplayAmount =
                requireNotNull(this.shouldPaymentButtonDisplayAmount)

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
