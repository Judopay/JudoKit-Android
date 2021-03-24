package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Object that contains information about the account details provided by the merchant.
 */
@Parcelize
class PrimaryAccountDetails internal constructor(
    var name: String?,
    var accountNumber: String?,
    var dateOfBirth: String?,
    var postCode: String?
) : Parcelable {

    /**
     * Builder class for creating a [PrimaryAccountDetails] instance.
     */
    class Builder {
        private var name: String? = null
        private var accountNumber: String? = null
        private var dateOfBirth: String? = null
        private var postCode: String? = null

        /**
         * Sets the name of the account.
         */
        fun setName(name: String?) = apply { this.name = name }

        /**
         * Sets the account number.
         */
        fun setAccountNumber(accountNumber: String?) = apply { this.accountNumber = accountNumber }

        /**
         * Sets the birth date.
         */
        fun setDateOfBirth(dateOfBirth: String?) = apply { this.dateOfBirth = dateOfBirth }

        /**
         * Sets post code.
         */
        fun setPostCode(postCode: String?) = apply { this.postCode = postCode }

        /**
         * Creates an instance of [PrimaryAccountDetails] based on provided data in setters.
         * @return An instance of [PrimaryAccountDetails].
         */
        fun build() = PrimaryAccountDetails(name, accountNumber, dateOfBirth, postCode)
    }

    override fun toString(): String {
        return "PrimaryAccountDetails(name=$name, accountNumber=$accountNumber, dateOfBirth=$dateOfBirth, postCode=$postCode)"
    }
}
