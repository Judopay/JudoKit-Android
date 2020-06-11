package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.requireNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
class UiConfiguration internal constructor(
    val avsEnabled: Boolean,
    val shouldDisplayAmount: Boolean,
    val shouldPaymentButtonDisplayAmount: Boolean
) : Parcelable {

    class Builder {
        private var avsEnabled: Boolean? = null
        private var shouldDisplayAmount: Boolean? = null
        private var shouldPaymentButtonDisplayAmount: Boolean? = null

        fun setAvsEnabled(enabled: Boolean?) = apply { this.avsEnabled = enabled }

        fun setShouldDisplayAmount(shouldDisplay: Boolean?) =
            apply { this.shouldDisplayAmount = shouldDisplay }

        fun setShouldPaymentButtonDisplayAmount(shouldPaymentButtonDisplayAmount: Boolean?) =
            apply { this.shouldPaymentButtonDisplayAmount = shouldPaymentButtonDisplayAmount }

        fun build(): UiConfiguration {
            val avsEnabled = requireNotNull(this.avsEnabled, "avsEnabled")
            val shouldDisplayAmount = requireNotNull(this.shouldDisplayAmount, "shouldDisplayAmount")
            val shouldPaymentButtonDisplayAmount = requireNotNull(this.shouldPaymentButtonDisplayAmount, "shouldPaymentButtonDisplayAmount")

            return UiConfiguration(avsEnabled, shouldDisplayAmount, shouldPaymentButtonDisplayAmount)
        }
    }

    override fun toString(): String {
        return "UiConfiguration(avsEnabled=$avsEnabled, shouldDisplayAmount=$shouldDisplayAmount, shouldPaymentButtonDisplayAmount=$shouldPaymentButtonDisplayAmount)"
    }
}
