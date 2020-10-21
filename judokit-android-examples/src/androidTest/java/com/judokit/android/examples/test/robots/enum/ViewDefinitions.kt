package com.judokit.android.examples.test.robots.enum

enum class ViewType(val value: String) {
    OPTION("option"),
    CELL("cell"),
    ITEM("item")
}

enum class View(val value: String) {
    CARD_NUMBER("Card Number"),
    CARDHOLDER_NAME("Cardholder Name"),
    EXPIRY_DATE("Expiry Date"),
    SECURE_CODE("Secure Code"),
    MAIN("Main"),
    RESULTS("Results"),
    INVALID_CARD_NUMBER("Invalid card number"),
    CHECK_EXPIRY_DATE("Check expiry date"),
    CHECK_CVV("Check CVV"),
}
