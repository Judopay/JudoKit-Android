package com.judokit.android.api.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * An Address linked to a card payment, used when address verification is performed.
 */
@Parcelize
class Address private constructor(
    var line1: String?,
    var line2: String?,
    var line3: String?,
    var town: String?,
    var billingCountry: String?,
    var postCode: String?
) : Parcelable {

    class Builder {
        private var line1: String? = null
        private var line2: String? = null
        private var line3: String? = null
        private var postCode: String? = null
        private var town: String? = null
        private var billingCountry: String? = null

        /**
         * Sets line one of the address.
         */
        fun setLine1(line1: String?) = apply { this.line1 = line1 }

        /**
         * Sets line two of the address.
         */
        fun setLine2(line2: String?) = apply { this.line2 = line2 }

        /**
         *  Sets line three of the address.
         */
        fun setLine3(line3: String?) = apply { this.line3 = line3 }

        /**
         * Sets town of the address.
         */
        fun setTown(town: String?) = apply { this.town = town }

        /**
         *  Sets billing country of the address.
         */
        fun setBillingCountry(country: String?) = apply { this.billingCountry = country }

        /**
         * Sets post code of the address.
         */
        fun setPostCode(postCode: String?) = apply { this.postCode = postCode }

        /**
         * Creates an instance of [Address] based on provided data in setters.
         * @return An instance of [Address]
         */
        fun build(): Address {
            return Address(line1, line2, line3, town, billingCountry, postCode)
        }
    }
}
