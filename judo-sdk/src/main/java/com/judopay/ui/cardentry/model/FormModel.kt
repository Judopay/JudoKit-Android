package com.judopay.ui.cardentry.model

data class FormModel(
        val cardNumber: String = "",
        val cardHolderName: String = "",
        val expirationDate: String = "",
        val securityNumber: String = "",
        val country: String = "",
        val postCode: String = ""
)

internal fun FormModel.getValueForFieldType(type: FormFieldType): String = when (type) {
    FormFieldType.NUMBER -> cardNumber
    FormFieldType.HOLDER_NAME -> cardHolderName
    FormFieldType.EXPIRATION_DATE -> expirationDate
    FormFieldType.SECURITY_NUMBER -> securityNumber
    FormFieldType.COUNTRY -> country
    FormFieldType.POST_CODE -> postCode
    else -> ""
}
