package com.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PBBAConfiguration internal constructor(
    val mobileNumber: String?,
    val emailAddress: String?,
    val appearsOnStatement: String?
) : Parcelable {
    class Builder {
        private var mobileNumber: String? = null
        private var emailAddress: String? = null
        private var appearsOnStatementAs: String? = null

        fun setMobileNumber(mobileNumber: String?) = apply { this.mobileNumber = mobileNumber }

        fun setAppearsOnStatementAs(appearsOnStatementAs: String?) =
            apply { this.appearsOnStatementAs = appearsOnStatementAs }

        fun setEmailAddress(emailAddress: String?) = apply { this.emailAddress = emailAddress }
    }

    fun build(): PBBAConfiguration =
        PBBAConfiguration(mobileNumber, emailAddress, appearsOnStatement)

    override fun toString(): String {
        return "PBBAConfiguration(mobileNumber=$mobileNumber, emailAddress=$emailAddress, appearsOnStatement=$appearsOnStatement)"
    }
}
