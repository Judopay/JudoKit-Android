package com.judokit.android.examples.test.card

object CardDetails {
    const val CARD_NUMBER = "4976 3500 0000 6891"
    const val CARDHOLDER_NAME = "Test User"
    const val CARD_EXPIRY = "12/30"
    const val CARD_SECURITY_CODE = "341"
    const val WRONG_CV2 = "123"
    const val FRICTIONLESS_SUCCESSFUL = "Frictionless Successful"
}

object FeaturesList {
    const val PAY_WITH_CARD_LABEL = "Pay with card"
    const val PREAUTH_WITH_CARD_LABEL = "Pre-auth with card"
    const val CHECK_CARD_LABEL = "Check card"
    const val TOKEN_PAYMENTS_LABEL = "Token payments"
    const val PAYMENT_METHODS_LABEL = "Payment methods"
    const val PREAUTH_METHODS_LABEL = "Pre-auth payment methods"
}

object Ravelin {
    const val TRA = "transactionRiskAnalysis"
    const val LOW_VALUE = "lowValue"
    const val NO_CHALLENGE = "noChallenge"
    const val NO_PREFERENCE = "noPreference"
    const val CHALLENGE_MANDATE = "challengeAsMandate"
    const val CHALLENGE_REQUESTED = "challengePreferred"
}

object Other {
    const val CANCEL_BUTTON = "Cancel"
    const val CANCELLED_PAYMENT_TOAST = "User cancelled the payment."
    const val TRANSACTION_PREVENTED = "The recommendation server has prevented this transaction."
    const val TRANSACTION_HALTED = "There was an error when retrieving the recommendation response."
    const val AUTH_CODE_PREFIX = "AuthCode: "
    const val COMPLETE_BUTTON = "COMPLETE"
    const val CARD_DECLINED_CV2_MSG = "Card declined: CV2 policy"
    const val THREE_DS_AUTH_FAILED_MSG = "Unable to process transaction. Card authentication failed with 3DS Server."
}

object Results {
    const val SUCCESS = "Success"
    const val DECLINED = "Declined"
    const val ERROR = "Error"
    const val PAYMENT = "Payment"
    const val PRE_AUTH = "PreAuth"
    const val CHECK_CARD = "CheckCard"
}

object Settings {
    const val CHALLENGE_NO_PREFERENCE = "NO_PREFERENCE"
    const val SCA_LOW_VALUE = "LOW_VALUE"
}

object BillingInfo {
    const val VALID_EMAIL = "user@test.com"
    const val VALID_MOBILE = "07812345678"
    const val VALID_ADDRESS = "235 Regent Street"
    const val VALID_CITY = "London"
    const val VALID_POSTCODE = "W1B 2EL"
    const val VALID_COUNTRY_CODE = "826"
    const val VALID_COUNTRY = "United Kingdom"
    const val INVALID_POSTCODE = "38GL112"
    const val SPECIAL_CHARACTERS = "#$@*"
    const val INVALID_POSTCODE_LABEL = "Invalid postcode entered"
    const val INVALID_ZIPCODE_LABEL = "Invalid ZIP code entered"
    const val INVALID_EMAIL_LABEL = "Please enter a valid email"
    const val INVALID_PHONE_LABEL = "Please enter a valid mobile number"
    const val INVALID_ADDRESS_LABEL = "Please enter a valid address"
    const val INVALID_CITY_LABEL = "Please enter a valid city"
}
