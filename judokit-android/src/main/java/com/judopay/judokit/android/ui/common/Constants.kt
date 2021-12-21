package com.judopay.judokit.android.ui.common

const val GOOGLE_PAY_API_VERSION = 2
const val GOOGLE_PAY_API_VERSION_MINOR = 0

const val ANIMATION_DURATION_500 = 500L
const val PATTERN_CARD_EXPIRATION_DATE = "##/##"
const val REGEX_JUDO_ID = "^(([0-9]{9})|([0-9]{3}-[0-9]{3}-[0-9]{3})|([0-9]{6}))?\$"

/**
 *  Constant for registering broadcast receiver
 *  Used in [com.judopay.judokit.android.ui.pollingstatus.PollingStatusFragment.handleBankSaleResponse] method for sending the broadcast
 */
const val BR_PBBA_RESULT = "BR_PBBA_RESULT"

/**
 *  Constant for acquiring JudoResult of PayByBank app sale response from an intent
 */
const val PBBA_RESULT = "PBBA_RESULT"

/**
 *  Constant to store the sdk version, injected in every Judo API request headers/body
 */
const val JUDO_KIT_VERSION = "2.2.0"

/**
 *  Constant to store the Judo API version, injected in every Judo API request headers
 */
const val JUDO_API_VERSION = "6.7.0"
