package com.judokit.android.examples.test.model

data class Card(
    val cardType: String,
    val cardNumber: String,
    val securityCode: String,
    val expiryDate: String,
    val cardHolder: String,
    val country: String,
    val secureCodeErrorMessage: String,
    val paymentMethodsSubtitle: String
)
