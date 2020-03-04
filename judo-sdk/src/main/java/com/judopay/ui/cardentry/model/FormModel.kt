package com.judopay.ui.cardentry.model

data class FormModel(
        val cardNumber: String = "",
        val cardHolderName: String = "",
        val expirationDate: String = "",
        val securityNumber: String = "",
        val country: String = "",
        val postCode: String = ""
) {
    override fun toString(): String {
        return "FormModel(cardNumber='$cardNumber', cardHolderName='$cardHolderName', expirationDate='$expirationDate', securityNumber='$securityNumber', country='$country', postCode='$postCode')"
    }
}
