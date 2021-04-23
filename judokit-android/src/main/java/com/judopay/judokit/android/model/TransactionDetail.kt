package com.judopay.judokit.android.model

class TransactionDetail private constructor(
    val cardNumber: String?,
    val cardHolderName: String?,
    val expirationDate: String?,
    val securityNumber: String?,
    val country: String?,
    val email: String?,
    val phoneCountryCode: String?,
    val mobileNumber: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val addressLine3: String?,
    val city: String?,
    val postalCode: String?
) {
    class Builder {
        private var cardNumber: String? = null
        private var cardHolderName: String? = null
        private var expirationDate: String? = null
        private var securityNumber: String? = null
        private var countryCode: String? = null
        private var email: String? = null
        private var phoneCountryCode: String? = null
        private var mobileNumber: String? = null
        private var addressLine1: String? = null
        private var addressLine2: String? = null
        private var addressLine3: String? = null
        private var city: String? = null
        private var postalCode: String? = null

        fun setCardNumber(cardNumber: String?) = apply { this.cardNumber = cardNumber }
        fun setCardHolderName(cardHolderName: String?) =
            apply { this.cardHolderName = cardHolderName }

        fun setExpirationDate(expirationDate: String?) =
            apply { this.expirationDate = expirationDate }

        fun setSecurityNumber(securityNumber: String?) =
            apply { this.securityNumber = securityNumber }

        fun setCountryCode(countryCode: String?) = apply { this.countryCode = countryCode }
        fun setEmail(email: String?) = apply { this.email = email }
        fun setPhoneCountryCode(phoneCountryCode: String?) =
            apply { this.phoneCountryCode = phoneCountryCode }

        fun setMobileNumber(mobileNumber: String?) = apply { this.mobileNumber = mobileNumber }
        fun setAddressLine1(addressLine1: String?) = apply { this.addressLine1 = addressLine1 }
        fun setAddressLine2(addressLine2: String?) = apply { this.addressLine2 = addressLine2 }
        fun setAddressLine3(addressLine3: String?) = apply { this.addressLine3 = addressLine3 }
        fun setCity(city: String?) = apply { this.city = city }
        fun setPostalCode(postalCode: String?) = apply { this.postalCode = postalCode }

        fun build() = TransactionDetail(
            cardNumber,
            cardHolderName,
            expirationDate,
            securityNumber,
            countryCode,
            email,
            phoneCountryCode,
            mobileNumber,
            addressLine1,
            addressLine2,
            addressLine3,
            city,
            postalCode
        )
    }
}
