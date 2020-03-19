package com.judopay.model

import android.os.Parcelable
import com.judopay.requireNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
class UiConfiguration internal constructor(val avsEnabled: Boolean) : Parcelable {

    class Builder {
        private var avsEnabled: Boolean? = null

        fun setAvsEnabled(enabled: Boolean?) = apply { this.avsEnabled = enabled }

        fun build(): UiConfiguration {
            val enabled = requireNotNull(avsEnabled, "avsEnabled")

            return UiConfiguration(enabled)
        }
    }

    override fun toString(): String {
        return "UiConfiguration(avsEnabled=$avsEnabled)"
    }
}
