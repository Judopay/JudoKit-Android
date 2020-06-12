package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.requireNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
class UiConfiguration internal constructor(
    val avsEnabled: Boolean,
    val shouldPaymentMethodsDisplayAmount: Boolean,
    val shouldPaymentMethodsVerifySecurityCode: Boolean
) : Parcelable {

    class Builder {
        private var avsEnabled: Boolean? = null
        private var shouldPaymentMethodsDisplayAmount: Boolean? = null
        private var shouldPaymentMethodsVerifySecurityCode: Boolean? = null

        fun setAvsEnabled(enabled: Boolean?) = apply { this.avsEnabled = enabled }

        fun setShouldPaymentMethodsDisplayAmount(shouldPaymentMethodsDisplayAmount: Boolean?) =
            apply { this.shouldPaymentMethodsDisplayAmount = shouldPaymentMethodsDisplayAmount }

        fun setShouldPaymentMethodsVerifySecurityCode(shouldPaymentMethodsVerifySecurityCode: Boolean?) =
            apply { this.shouldPaymentMethodsVerifySecurityCode = shouldPaymentMethodsVerifySecurityCode }

        fun build(): UiConfiguration {
            val avsEnabled = requireNotNull(this.avsEnabled, "avsEnabled")
            val shouldDisplayAmount =
                requireNotNull(this.shouldPaymentMethodsDisplayAmount, "shouldPaymentMethodsDisplayAmount")
            val shouldPaymentMethodsVerifySecurityCode =
                requireNotNull(this.shouldPaymentMethodsVerifySecurityCode, "shouldPaymentMethodsVerifySecurityCode")

            return UiConfiguration(avsEnabled, shouldDisplayAmount, shouldPaymentMethodsVerifySecurityCode)
        }
    }

    override fun toString(): String {
        return "UiConfiguration(avsEnabled=$avsEnabled, shouldDisplayAmount=$shouldPaymentMethodsDisplayAmount, shouldEnterSecurityCode=$shouldPaymentMethodsVerifySecurityCode)"
    }
}
