package com.judopay.judokit.android.ui.cardentry.model

data class FormModel(
    val cardDetailsInputModel: CardDetailsInputModel,
    val billingDetailsInputModel: BillingDetailsInputModel,
)

fun CardDetailsInputModel.valueOfFieldWithType(type: CardDetailsFieldType): String =
    when (type) {
        CardDetailsFieldType.NUMBER -> cardNumber
        CardDetailsFieldType.HOLDER_NAME -> cardHolderName
        CardDetailsFieldType.EXPIRATION_DATE -> expirationDate
        CardDetailsFieldType.SECURITY_NUMBER -> securityNumber
        CardDetailsFieldType.COUNTRY -> country
        CardDetailsFieldType.POST_CODE -> postCode
    }

fun BillingDetailsInputModel.valueOfBillingDetailsFieldWithType(type: BillingDetailsFieldType): String =
    when (type) {
        BillingDetailsFieldType.COUNTRY -> countryCode
        BillingDetailsFieldType.STATE -> state
        BillingDetailsFieldType.POST_CODE -> postalCode
        BillingDetailsFieldType.EMAIL -> email
        BillingDetailsFieldType.PHONE_COUNTRY_CODE -> phoneCountryCode
        BillingDetailsFieldType.MOBILE_NUMBER -> mobileNumber
        BillingDetailsFieldType.ADDRESS_LINE_1 -> addressLine1
        BillingDetailsFieldType.ADDRESS_LINE_2 -> addressLine2
        BillingDetailsFieldType.ADDRESS_LINE_3 -> addressLine3
        BillingDetailsFieldType.CITY -> city
    }
