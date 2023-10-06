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
const val JUDO_KIT_VERSION = "4.1.3"

/**
 *  Constant to store the Judo API version, injected in every Judo API request headers
 */
const val JUDO_API_VERSION = "6.19.0"

const val POSTAL_CODE_MIN_LENGTH_CA = 6
const val POSTAL_CODE_MAX_LENGTH_CA = 7
const val POSTAL_CODE_MIN_LENGTH_UK = 5
const val POSTAL_CODE_MAX_LENGTH_UK = 8
const val POSTAL_CODE_MIN_LENGTH_USA = 5
const val POSTAL_CODE_MAX_LENGTH_USA = 10
const val POSTAL_CODE_MIN_LENGTH_OTHER = 1
const val POSTAL_CODE_MAX_LENGTH_OTHER = 16

const val REG_EX_GB_POST_CODE = "^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}\$"
const val REG_EX_US_POST_CODE = "^(\\d{5}(?:-\\d{4})?)\$"
const val REG_EX_CA_POST_CODE = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]\$"
const val REG_EX_OTHER_POST_CODE = "^[A-Z0-9 -]{1,16}\$"
const val REG_EX_CARDHOLDER_NAME = "^[A-Za-z.\\-'\\s?]+\$"
const val REG_EX_CITY = "^[A-Za-z.'\\- ]+\$"
const val REG_EX_MOBILE_NUMBER = "^.{10,}\$"
const val REG_EX_ADDRESS_LINE = "^[a-zA-Z0-9,./'\\- ]+\$"

const val RECOMMENDATION_API_DEFAULT_TIMEOUT_SECONDS = 30
