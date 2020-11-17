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
    CHECK_EXPIRY_DATE("Invalid date value entered"),
    INVALID_POST_CODE("Invalid postcode entered"),
    SUBMIT_BUTTON("Submit"),
    CARD_HEADER("Card Header"),
    GOOGLE_PAY_HEADER("Google Pay Header"),
    IDEAL_HEADER("iDEAL Header"),
    PBBA_HEADER("PbBa Header"),
    CARD_PAYMENT_METHOD("Card payment method"),
    GOOGLE_PAY_PAYMENT_METHOD("Google Pay payment method"),
    IDEAL_PAYMENT_METHOD("iDEAL payment method"),
    PAYMENT_METHODS("Payment Methods")
}
