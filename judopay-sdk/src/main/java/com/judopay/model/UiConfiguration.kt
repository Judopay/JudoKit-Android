package com.judopay.model

import android.os.Parcelable
import com.judopay.requireNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
class UiConfiguration internal constructor(
    val avsEnabled: Boolean,
    val shouldDisplayAmount: Boolean
) : Parcelable {

    class Builder {
        private var avsEnabled: Boolean? = null
        private var shouldDisplayAmount: Boolean? = null

        fun setAvsEnabled(enabled: Boolean?) = apply { this.avsEnabled = enabled }

        fun setShouldDisplayAmount(shouldDisplay: Boolean?) =
            apply { this.shouldDisplayAmount = shouldDisplay }

        fun build(): UiConfiguration {
            val avsEnabled = requireNotNull(this.avsEnabled, "avsEnabled")
            val shouldDisplayAmount = requireNotNull(this.shouldDisplayAmount, "shouldDisplayAmount")

            return UiConfiguration(avsEnabled, shouldDisplayAmount)
        }
    }

    override fun toString(): String {
        return "UiConfiguration(avsEnabled=$avsEnabled)"
    }
}
