package com.judopay.api.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.judopay.api.error.ApiError
import kotlinx.android.parcel.Parcelize

/**
 * Base class for all HTTP responses that come back from the Judo API, this contains fields
 * that are common to all responses, such as the result, message and details of any errors that
 * occurred.
 */

//TODO: Refactor the stuff below to a enum class
const val GENERAL_ERROR = 0
const val GENERAL_MODEL_ERROR = 1
const val UNAUTHORIZED = 7
const val PAYMENT_SYSTEM_ERROR = 9
const val PAYMENT_DECLINED = 11
const val PAYMENT_FAILED = 12
const val TRANSACTION_NOT_FOUND = 19
const val VALIDATION_PASSED = 20
const val UNCAUGHT_ERROR = 21
const val SERVER_ERROR = 22
const val INVALID_FROM_DATE = 23
const val INVALID_TO_DATE = 24
const val CANT_FIND_WEB_PAYMENT = 25
const val GENERAL_ERROR_SIMPLE_APPLICATION = 26
const val INVALID_API_VERSION = 40
const val MISSING_API_VERSION = 41
const val PRE_AUTH_EXPIRED = 42
const val COLLECTION_ORIGINAL_TRANSACTION_WRONG_TYPE = 43
const val CURRENCY_MUST_EQUAL_ORIGINAL_TRANSACTION = 44
const val CANNOT_COLLECT_A_VOIDED_TRANSACTION = 45
const val COLLECTION_EXCEEDS_PRE_AUTH = 46
const val REFUND_ORIGINAL_TRANSACTION_WRONG_TYPE = 47
const val CANNOT_REFUND_A_VOIDED_TRANSACTION = 48
const val REFUND_EXCEEDS_ORIGINAL_TRANSACTION = 49
const val VOID_ORIGINAL_TRANSACTION_WRONG_TYPE = 50
const val VOID_ORIGINAL_TRANSACTION_IS_ALREADY_VOID = 51
const val VOID_ORIGINAL_TRANSACTION_HAS_BEEN_COLLECTED = 52
const val VOID_ORIGINAL_TRANSACTION_AMOUNT_NOT_EQUAL_TO_PRE_AUTH = 53
const val UNABLE_TO_ACCEPT = 54
const val ACCOUNT_LOCATION_NOT_FOUND = 55
const val ACCESS_DENIED_TO_TRANSACTION = 56
const val NO_CONSUMER_FOR_TRANSACTION = 57
const val TRANSACTION_NOT_ENROLLED_IN_THREE_D_SECURE = 58
const val TRANSACTION_ALREADY_AUTHORIZED_BY_THREE_D_SECURE = 59
const val THREE_D_SECURE_NOT_SUCCESSFUL = 60
const val AP_UNABLE_TO_DECRYPT = 61
const val REFERENCED_TRANSACTION_NOT_FOUND = 62
const val REFERENCED_TRANSACTION_NOT_SUCCESSFUL = 63
const val TEST_CARD_NOT_ALLOWED = 64
const val COLLECTION_NOT_VALID = 65
const val REFUND_ORIGINAL_TRANSACTION_NULL = 66
const val REFUND_NOT_VALID = 67
const val VOID_NOT_VALID = 68
const val UNKNOWN = 69
const val CARD_TOKEN_INVALID = 70
const val UNKNOWN_PAYMENT_MODEL = 71
const val UNABLE_TO_ROUTE_TRANSACTION = 72
const val CARD_TYPE_NOT_SUPPORTED = 73
const val CARD_CV2_INVALID = 74
const val CARD_TOKEN_DOESNT_MATCH_CONSUMER = 75
const val WEB_PAYMENT_REFERENCE_INVALID = 76
const val WEB_PAYMENT_ACCOUNT_LOCATION_NOT_FOUND = 77
const val REGISTER_CARD_WITH_WRONG_TRANSACTION_TYPE = 78
const val INVALID_AMOUNT_TO_REGISTER_CARD = 79
const val CONTENT_TYPE_NOT_SPECIFIED_OR_UNSUPPORTED = 80
const val INTERNAL_ERROR_AUTHENTICATING = 81
const val TRANSACTION_NOT_FOUND_1 = 82
const val RESOURCE_NOT_FOUND = 83
const val LACK_OF_PERMISSIONS_UNAUTHORIZED = 84
const val CONTENT_TYPE_NOT_SUPPORTED = 85
const val AUTHENTICATION_FAILURE = 403
const val NOT_FOUND = 404
const val MUST_PROCESS_PRE_AUTH_BY_TOKEN = 4002
const val APPLICATION_MODEL_IS_NULL = 20000
const val APPLICATION_MODEL_REQUIRES_REFERENCE = 20001
const val APPLICATION_HAS_ALREADY_GONE_LIVE = 20002
const val MISSING_PRODUCT_SELECTION = 20003
const val ACCOUNT_NOT_IN_SANDBOX = 20004
const val APPLICATION_REC_ID_REQUIRED = 20005
const val REQUEST_NOT_PROPERLY_FORMATTED = 20006
const val NO_APPLICATION_REFERENCE_FOUND = 20007
const val NOT_SUPPORTED_FILE_TYPE = 20008
const val ERROR_WITH_FILE_UPLOAD = 20009
const val EMPTY_APPLICATION_REFERENCE = 20010
const val APPLICATION_DOES_NOT_EXIST = 20011
const val UNKNOWN_SORT_SPECIFIED = 20013
const val PAGE_SIZE_LESS_THAN_ONE = 20014
const val PAGE_SIZE_MORE_THAN_FIVE_HUNDRED = 20015
const val OFFSET_LESS_THAN_ZERO = 20016
const val INVALID_MERCHANT_ID = 20017
const val MERCHANT_ID_NOT_FOUND = 20018
const val NO_PRODUCTS_WERE_FOUND = 20019
const val ONLY_THE_JUDO_PARTNER_CAN_SUBMIT_SIMPLE_APPLICATIONS = 20020
const val UNABLE_TO_PARSE_DOCUMENT = 20021
const val UNABLE_TO_FIND_A_DEFAULT_ACCOUNT_LOCATION = 20022
const val WEB_PAYMENTS_SHOULD_BE_CREATED_BY_POSTING_TO_URL = 20023
const val INVALID_MD = 20025
const val INVALID_RECEIPT_ID = 20026

private const val RESPONSE_STATUS_SUCCESS = "Success"
private const val RESPONSE_STATUS_DECLINED = "Declined"

@Parcelize
open class Response(

        val result: String? = null,

        @SerializedName("category")
        val errorCategory: Int? = null,

        @SerializedName("explanation")
        val errorExplanation: String? = null,

        @SerializedName("resolution")
        val errorResolution: String? = null,

        @SerializedName("message")
        val message: String? = null,

        @SerializedName("code")
        val errorCode: String? = null,

        @SerializedName("details")
        val errorDetails: List<ApiError>? = null

) : Parcelable {

    val isSuccess: Boolean
        get() = RESPONSE_STATUS_SUCCESS == result

    val isDeclined: Boolean
        get() = RESPONSE_STATUS_DECLINED == result

    override fun toString(): String {
        return "Response(result=$result, errorCategory=$errorCategory, errorExplanation=$errorExplanation, errorResolution=$errorResolution, message=$message, errorCode=$errorCode, errorDetails=$errorDetails)"
    }
}