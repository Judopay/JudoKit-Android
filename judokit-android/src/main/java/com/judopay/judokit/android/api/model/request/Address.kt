package com.judopay.judokit.android.api.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * An Address linked to a card payment, used when address verification is performed.
 */
@Parcelize
@Suppress("LongParameterList")
class Address internal constructor(
    @SerializedName("address1") var line1: String?,
    @SerializedName("address2") var line2: String?,
    @SerializedName("address3") var line3: String?,
    var town: String?,
    var billingCountry: String?,
    var postCode: String?,
    var countryCode: Int?,
    @SerializedName("state") var administrativeDivision: String?,
) : Parcelable {
    @Deprecated("Please use administrativeDivision instead", replaceWith = ReplaceWith("administrativeDivision"))
    var state: String?
        get() = administrativeDivision
        set(value) {
            administrativeDivision = value
        }

    class Builder {
        private var line1: String? = null
        private var line2: String? = null
        private var line3: String? = null
        private var postCode: String? = null
        private var town: String? = null
        private var billingCountry: String? = null
        private var countryCode: Int? = null
        private var administrativeDivision: String? = null

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
         * Sets country code of the address.
         */
        fun setCountryCode(code: Int?) = apply { this.countryCode = code }

        /**
         * Sets post code of the address.
         */
        fun setPostCode(postCode: String?) = apply { this.postCode = postCode }

        /**
         * Sets state of the address.
         */
        @Deprecated(
            "Please use setAdministrativeDivision instead",
            replaceWith = ReplaceWith("setAdministrativeDivision(administrativeDivision)"),
        )
        fun setState(state: String?) = apply { this.administrativeDivision = state }

        /**
         * Sets administrative area of the address.
         */
        fun setAdministrativeDivision(administrativeDivision: String?) = apply { this.administrativeDivision = administrativeDivision }

        /**
         * Creates an instance of [Address] based on provided data in setters.
         * @return An instance of [Address]
         */
        fun build(): Address {
            return Address(
                line1 = line1,
                line2 = line2,
                line3 = line3,
                town = town,
                billingCountry = billingCountry,
                postCode = postCode,
                countryCode = countryCode,
                administrativeDivision = if (administrativeDivision.isNullOrBlank()) null else administrativeDivision,
            )
        }
    }
}
