package com.judopay.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PrimaryAccountDetails internal constructor(
        var name: String?,
        var accountNumber: String?,
        var dateOfBirth: String?,
        var postCode: String?
) : Parcelable {

    class Builder {
        private var name: String? = null
        private var accountNumber: String? = null
        private var dateOfBirth: String? = null
        private var postCode: String? = null

        fun setName(name: String?) = apply { this.name = name }

        fun setAccountNumber(accountNumber: String?) = apply { this.accountNumber = accountNumber }

        fun setDateOfBirth(dateOfBirth: String?) = apply { this.dateOfBirth = dateOfBirth }

        fun setPostCode(postCode: String?) = apply { this.postCode = postCode }

        fun build() = PrimaryAccountDetails(name, accountNumber, dateOfBirth, postCode)
    }

}