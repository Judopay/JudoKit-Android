package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.requireNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
class UiConfiguration internal constructor(
    val avsEnabled: Boolean,
    val shouldDisplayAmount: Boolean,
    val shouldPaymentWidgetVerifySecurityCode: Boolean
) : Parcelable {

    class Builder {
        private var avsEnabled: Boolean? = null
        private var shouldDisplayAmount: Boolean? = null
        private var shouldPaymentWidgetVerifySecurityCode: Boolean? = null

        fun setAvsEnabled(enabled: Boolean?) = apply { this.avsEnabled = enabled }

        fun setShouldDisplayAmount(shouldDisplay: Boolean?) =
            apply { this.shouldDisplayAmount = shouldDisplay }

        fun setShouldPaymentWidgetVerifySecurityCode(shouldPaymentWidgetVerifySecurityCode: Boolean?) =
            apply { this.shouldPaymentWidgetVerifySecurityCode = shouldPaymentWidgetVerifySecurityCode }

        fun build(): UiConfiguration {
            val avsEnabled = requireNotNull(this.avsEnabled, "avsEnabled")
            val shouldDisplayAmount =
                requireNotNull(this.shouldDisplayAmount, "shouldDisplayAmount")
            val shouldPaymentWidgetVerifySecurityCode =
                requireNotNull(this.shouldPaymentWidgetVerifySecurityCode, "shouldPaymentWidgetVerifySecurityCode")

            return UiConfiguration(avsEnabled, shouldDisplayAmount, shouldPaymentWidgetVerifySecurityCode)
        }
    }

    override fun toString(): String {
        return "UiConfiguration(avsEnabled=$avsEnabled, shouldDisplayAmount=$shouldDisplayAmount, shouldEnterSecurityCode=$shouldPaymentWidgetVerifySecurityCode)"
    }
}
