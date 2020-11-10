package com.judokit.android.examples.test.robots.enum

enum class ViewType(val value: String) {
    OPTION("option"),
    CELL("cell"),
    ITEM("item"),
    TEXT_FIELD("text field")
}

enum class View(val value: String) {
    CARD_NUMBER("Card Number"),
    CARDHOLDER_NAME("Cardholder Name"),
    EXPIRY_DATE("Expiry Date"),
    SECURE_CODE("Secure Code"),
    COUNTRY("Country"),
    POST_CODE("Post Code"),
    MAIN("Main"),
    RESULTS("Results"),
    INVALID_CARD_NUMBER("Invalid card number"),
    CHECK_EXPIRY_DATE("Check expiry date"),
    CHECK_CVV("Check CVV"),
    INVALID_POST_CODE("Invalid postcode entered"),
    SUBMIT_BUTTON("Submit")
}