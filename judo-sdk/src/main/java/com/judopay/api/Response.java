package com.judopay.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all HTTP responses that come back from the JudoPay API, this contains fields
 * that are common to all responses, such as the result, message and details of any errors that
 * occurred.
 */
public class Response implements Parcelable {

    private String result;

    @SerializedName("category")
    private Integer errorCategory;

    @SerializedName("explanation")
    private String errorExplanation;

    @SerializedName("resolution")
    private String errorResolution;

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private String errorCode;

    @SerializedName("details")
    private List<ApiError> errorDetails;

    public boolean isSuccess() {
        return "Success".equals(result);
    }

    public boolean isDeclined() {
        return "Declined".equals(result);
    }

    public String getResult() {
        return result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public Integer getErrorCategory() {
        return errorCategory;
    }

    public String getErrorExplanation() {
        return errorExplanation;
    }

    public String getErrorResolution() {
        return errorResolution;
    }

    public List<ApiError> getErrorDetails() {
        return errorDetails;
    }

    public static final int GENERAL_ERROR = 0;
    public static final int GENERAL_MODEL_ERROR = 1;
    public static final int UNAUTHORIZED = 7;
    public static final int PAYMENT_SYSTEM_ERROR = 9;
    public static final int PAYMENT_DECLINED = 11;
    public static final int PAYMENT_FAILED = 12;
    public static final int TRANSACTION_NOT_FOUND = 19;
    public static final int VALIDATION_PASSED = 20;
    public static final int UNCAUGHT_ERROR = 21;
    public static final int SERVER_ERROR = 22;
    public static final int INVALID_FROM_DATE = 23;
    public static final int INVALID_TO_DATE = 24;
    public static final int CANT_FIND_WEB_PAYMENT = 25;
    public static final int GENERAL_ERROR_SIMPLE_APPLICATION = 26;
    public static final int INVALID_API_VERSION = 40;
    public static final int MISSING_API_VERSION = 41;
    public static final int PRE_AUTH_EXPIRED = 42;
    public static final int COLLECTION_ORIGINAL_TRANSACTION_WRONG_TYPE = 43;
    public static final int CURRENCY_MUST_EQUAL_ORIGINAL_TRANSACTION = 44;
    public static final int CANNOT_COLLECT_A_VOIDED_TRANSACTION = 45;
    public static final int COLLECTION_EXCEEDS_PRE_AUTH = 46;
    public static final int REFUND_ORIGINAL_TRANSACTION_WRONG_TYPE = 47;
    public static final int CANNOT_REFUND_A_VOIDED_TRANSACTION = 48;
    public static final int REFUND_EXCEEDS_ORIGINAL_TRANSACTION = 49;
    public static final int VOID_ORIGINAL_TRANSACTION_WRONG_TYPE = 50;
    public static final int VOID_ORIGINAL_TRANSACTION_IS_ALREADY_VOID = 51;
    public static final int VOID_ORIGINAL_TRANSACTION_HAS_BEEN_COLLECTED = 52;
    public static final int VOID_ORIGINAL_TRANSACTION_AMOUNT_NOT_EQUAL_TO_PREAUTH = 53;
    public static final int UNABLE_TO_ACCEPT = 54;
    public static final int ACCOUNT_LOCATION_NOT_FOUND = 55;
    public static final int ACCESS_DENIED_TO_TRANSACTION = 56;
    public static final int NO_CONSUMER_FOR_TRANSACTION = 57;
    public static final int TRANSACTION_NOT_ENROLLED_IN_THREEDSECURE = 58;
    public static final int TRANSACTION_ALREADY_AUTHORIZED_BY_THREEDSECURE = 59;
    public static final int THREEDSECURE_NOT_SUCCESSFUL = 60;
    public static final int AP_UNABLE_TO_DECRYPT = 61;
    public static final int REFERENCED_TRANSACTION_NOT_FOUND = 62;
    public static final int REFERENCED_TRANSACTION_NOT_SUCCESSFUL = 63;
    public static final int TEST_CARD_NOT_ALLOWED = 64;
    public static final int COLLECTION_NOT_VALID = 65;
    public static final int REFUND_ORIGINAL_TRANSACTION_NULL = 66;
    public static final int REFUND_NOT_VALID = 67;
    public static final int VOID_NOT_VALID = 68;
    public static final int UNKNOWN = 69;
    public static final int CARD_TOKEN_INVALID = 70;
    public static final int UNKNOWN_PAYMENT_MODEL = 71;
    public static final int UNABLE_TO_ROUTE_TRANSACTION = 72;
    public static final int CARD_TYPE_NOT_SUPPORTED = 73;
    public static final int CARD_CV2_INVALID = 74;
    public static final int CARD_TOKEN_DOESNT_MATCH_CONSUMER = 75;
    public static final int WEB_PAYMENT_REFERENCE_INVALID = 76;
    public static final int WEB_PAYMENT_ACCOUNT_LOCATION_NOT_FOUND = 77;
    public static final int REGISTER_CARD_WITH_WRONG_TRANSACTION_TYPE = 78;
    public static final int INVALID_AMOUNT_TO_REGISTER_CARD = 79;
    public static final int CONTENT_TYPE_NOT_SPECIFIED_OR_UNSUPPORTED = 80;
    public static final int INTERNAL_ERROR_AUTHENTICATING = 81;
    public static final int TRANSACTION_NOT_FOUND_1 = 82;
    public static final int RESOURCE_NOT_FOUND = 83;
    public static final int LACK_OF_PERMISSIONS_UNAUTHORIZED = 84;
    public static final int CONTENT_TYPE_NOT_SUPPORTED = 85;
    public static final int AUTHENTICATION_FAILURE = 403;
    public static final int NOT_FOUND = 404;
    public static final int MUST_PROCESS_PRE_AUTH_BY_TOKEN = 4002;
    public static final int APPLICATION_MODEL_IS_NULL = 20000;
    public static final int APPLICATION_MODEL_REQUIRES_REFERENCE = 20001;
    public static final int APPLICATION_HAS_ALREADY_GONE_LIVE = 20002;
    public static final int MISSING_PRODUCT_SELECTION = 20003;
    public static final int ACCOUNT_NOT_IN_SANDBOX = 20004;
    public static final int APPLICATION_REC_ID_REQUIRED = 20005;
    public static final int REQUEST_NOT_PROPERLY_FORMATTED = 20006;
    public static final int NO_APPLICATION_REFERENCE_FOUND = 20007;
    public static final int NOT_SUPPORTED_FILE_TYPE = 20008;
    public static final int ERROR_WITH_FILE_UPLOAD = 20009;
    public static final int EMPTY_APPLICATION_REFERENCE = 20010;
    public static final int APPLICATION_DOES_NOT_EXIST = 20011;
    public static final int UNKNOWN_SORT_SPECIFIED = 20013;
    public static final int PAGE_SIZE_LESS_THAN_ONE = 20014;
    public static final int PAGE_SIZE_MORE_THAN_FIVE_HUNDRED = 20015;
    public static final int OFFSET_LESS_THAN_ZERO = 20016;
    public static final int INVALID_MERCHANT_ID = 20017;
    public static final int MERCHANT_ID_NOT_FOUND = 20018;
    public static final int NO_PRODUCTS_WERE_FOUND = 20019;
    public static final int ONLY_THE_JUDO_PARTNER_CAN_SUBMIT_SIMPLE_APPLICATIONS = 20020;
    public static final int UNABLE_TO_PARSE_DOCUMENT = 20021;
    public static final int UNABLE_TO_FIND_A_DEFAULT_ACCOUNT_LOCATION = 20022;
    public static final int WEB_PAYMENTS_SHOULD_BE_CREATED_BY_POSTING_TO_URL = 20023;
    public static final int INVALI_DMD = 20025;
    public static final int INVALID_RECEIPT_ID = 20026;

    @Override
    public String toString() {
        return "Response{" +
                "errorDetails=" + errorDetails +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", errorResolution='" + errorResolution + '\'' +
                ", errorExplanation='" + errorExplanation + '\'' +
                ", errorCategory=" + errorCategory +
                ", result='" + result + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.result);
        dest.writeValue(this.errorCategory);
        dest.writeString(this.errorExplanation);
        dest.writeString(this.errorResolution);
        dest.writeString(this.message);
        dest.writeString(this.errorCode);
        dest.writeTypedList(errorDetails);
    }

    public Response() {
    }

    protected Response(Parcel in) {
        this.result = in.readString();
        this.errorCategory = (Integer) in.readValue(Integer.class.getClassLoader());
        this.errorExplanation = in.readString();
        this.errorResolution = in.readString();
        this.message = in.readString();
        this.errorCode = in.readString();
        this.errorDetails = in.createTypedArrayList(ApiError.CREATOR);
    }

    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        public Response createFromParcel(Parcel source) {
            return new Response(source);
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}