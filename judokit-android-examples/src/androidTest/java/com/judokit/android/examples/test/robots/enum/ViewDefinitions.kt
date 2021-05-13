package com.judokit.android.examples.test.robots.enum

enum class ViewType(val value: String) {
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
    DISMISS_BUTTON("Dismiss"),
    CARD_HEADER("Card Header"),
    GOOGLE_PAY_HEADER("Apple Pay/Google Pay Header"),
    IDEAL_HEADER("iDEAL Header"),
    PBBA_HEADER("PbBa Header"),
    CARD_SELECTOR("Card Selector"),
    GOOGLE_PAY_SELECTOR("Apple Pay/Google Pay Selector"),
    IDEAL_SELECTOR("iDEAL Selector"),
    PAYMENT_METHODS("payment methods")
}
