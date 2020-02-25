package com.judopay.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class GooglePayConfiguration internal constructor() : Parcelable {

    class Builder {
        fun build(): GooglePayConfiguration {
            return GooglePayConfiguration()
        }
    }


}
