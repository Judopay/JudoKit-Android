package com.judokit.android.examples.test.model

data class Card(
    val cardType: String,
    val cardNumber: String,
    val securityCode: String,
    val expiryDate: String,
    val cardHolder: String,
    val country: String,
    val postCode: String,
    val expectedPostCode: String,
    val secureCodeErrorMessage: String
)
