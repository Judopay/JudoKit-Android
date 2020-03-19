package com.judopay.api.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * An Address linked to a payment card, used when address verification is performed.
 */
@Parcelize
class Address private constructor(
    var line1: String?,
    var line2: String?,
    var line3: String?,
    var town: String?,
    var countryCode: Int,
    var postCode: String?
) : Parcelable {

    class Builder {
        private var line1: String? = null
        private var line2: String? = null
        private var line3: String? = null
        private var postCode: String? = null
        private var town: String? = null
        private var countryCode = 0

        fun setLine1(line1: String?) = apply { this.line1 = line1 }

        fun setLine2(line2: String?) = apply { this.line2 = line2 }

        fun setLine3(line3: String?) = apply { this.line3 = line3 }

        fun setTown(town: String?) = apply { this.town = town }

        fun setCountryCode(countryCode: Int) = apply { this.countryCode = countryCode }

        fun setPostCode(postCode: String?) = apply { this.postCode = postCode }

        fun build(): Address {
            return Address(line1, line2, line3, town, countryCode, postCode)
        }
    }
}
