package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.requireNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
class UiConfiguration internal constructor(
    val avsEnabled: Boolean,
    val shouldDisplayAmount: Boolean,
    val shouldEnterSecurityCode: Boolean
) : Parcelable {

    class Builder {
        private var avsEnabled: Boolean? = null
        private var shouldDisplayAmount: Boolean? = null
        private var shouldEnterSecurityCode: Boolean? = null

        fun setAvsEnabled(enabled: Boolean?) = apply { this.avsEnabled = enabled }

        fun setShouldDisplayAmount(shouldDisplay: Boolean?) =
            apply { this.shouldDisplayAmount = shouldDisplay }

        fun setShouldEnterSecurityCode(shouldEnterSecurityCode: Boolean?) =
            apply { this.shouldEnterSecurityCode = shouldEnterSecurityCode }

        fun build(): UiConfiguration {
            val avsEnabled = requireNotNull(this.avsEnabled, "avsEnabled")
            val shouldDisplayAmount =
                requireNotNull(this.shouldDisplayAmount, "shouldDisplayAmount")
            val shouldEnterSecurityCode =
                requireNotNull(this.shouldEnterSecurityCode, "shouldEnterSecurityCode")

            return UiConfiguration(avsEnabled, shouldDisplayAmount, shouldEnterSecurityCode)
        }
    }

    override fun toString(): String {
        return "UiConfiguration(avsEnabled=$avsEnabled, shouldDisplayAmount=$shouldDisplayAmount, shouldEnterSecurityCode=$shouldEnterSecurityCode)"
    }
}
