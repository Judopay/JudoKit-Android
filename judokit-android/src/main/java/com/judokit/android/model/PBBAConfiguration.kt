package com.judokit.android.model

import android.net.Uri
import android.os.Parcelable
import com.judokit.android.requireNotNull
import kotlinx.android.parcel.Parcelize

@Parcelize
class PBBAConfiguration internal constructor(
    val mobileNumber: String?,
    val emailAddress: String?,
    val appearsOnStatement: String?,
    val deepLinkURL: Uri?,
    val deepLinkScheme: String
) : Parcelable {

    class Builder {
        private var mobileNumber: String? = null
        private var emailAddress: String? = null
        private var appearsOnStatementAs: String? = null
        private var deepLinkURL: Uri? = null
        private var deepLinkScheme: String? = null

        fun setMobileNumber(mobileNumber: String?) = apply { this.mobileNumber = mobileNumber }

        fun setAppearsOnStatementAs(appearsOnStatementAs: String?) =
            apply { this.appearsOnStatementAs = appearsOnStatementAs }

        fun setEmailAddress(emailAddress: String?) = apply { this.emailAddress = emailAddress }

        fun setDeepLinkURL(deepLinkURL: Uri?) = apply { this.deepLinkURL = deepLinkURL }

        fun setDeepLinkScheme(deepLinkScheme: String?) =
            apply { this.deepLinkScheme = deepLinkScheme }

        fun build(): PBBAConfiguration {
            val myDeepLinkScheme = requireNotNull(
                deepLinkScheme,
                "deepLinkScheme",
                "PBBA transactions require the deeplink scheme to be set. Either the app's URL Scheme or the deeplink scheme parameter has not been set."
            )

            return PBBAConfiguration(
                mobileNumber,
                emailAddress,
                appearsOnStatementAs,
                deepLinkURL,
                myDeepLinkScheme
            )
        }
    }

    override fun toString(): String {
        return "PBBAConfiguration(mobileNumber=$mobileNumber, emailAddress=$emailAddress, appearsOnStatement=$appearsOnStatement, deepLinkURL=$deepLinkURL, deepLinkScheme=$deepLinkScheme)"
    }
}
