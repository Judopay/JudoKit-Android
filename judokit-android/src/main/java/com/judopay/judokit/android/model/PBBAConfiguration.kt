package com.judopay.judokit.android.model

import android.net.Uri
import android.os.Parcelable
import com.judopay.judokit.android.requireNotNull
import kotlinx.parcelize.Parcelize

/**
 * A configuration class responsible for setting additional parameters for
 * Pay by Bank app payment method.
 */
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

        /**
         * Sets the consumer's mobile number.
         */
        fun setMobileNumber(mobileNumber: String?) = apply { this.mobileNumber = mobileNumber }

        /**
         * Sets how the consumer should appear on a statement.
         */
        fun setAppearsOnStatementAs(appearsOnStatementAs: String?) =
            apply { this.appearsOnStatementAs = appearsOnStatementAs }

        /**
         * Sets consumer's email address.
         */
        fun setEmailAddress(emailAddress: String?) = apply { this.emailAddress = emailAddress }

        /**
         * Sets the deep-link URL acquired from banking app after redirect, located in data field
         * of the intent object.
         * ```
         * intent.data
         * ```
         */
        fun setDeepLinkURL(deepLinkURL: Uri?) = apply { this.deepLinkURL = deepLinkURL }

        /**
         * Sets the deep-link scheme configured in AndroidManifest.xml file.
         */
        fun setDeepLinkScheme(deepLinkScheme: String?) =
            apply { this.deepLinkScheme = deepLinkScheme }

        /**
         * Creates an instance of [PBBAConfiguration] based on provided data in setters.
         * @throws IllegalArgumentException If deepLinkScheme is null.
         * @return An instance of [PBBAConfiguration]
         */
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
