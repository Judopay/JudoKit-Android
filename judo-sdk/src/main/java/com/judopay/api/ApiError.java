package com.judopay.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A single error instance that occurred when calling the JudoPay API, in response to performing
 * a request, most likely for a type of transaction such as a payment, pre-auth or token payment.
 */
public class ApiError implements Parcelable {

    public ApiError() { }

    private Integer code;
    private String fieldName;
    private String message;
    private String detail;

    public static final int JUDO_ID_NOT_SUPPLIED = 0;
    public static final int JUDO_ID_NOT_SUPPLIED_1 = 1;
    public static final int JUDO_ID_NOT_VALID = 2;
    public static final int JUDO_ID_NOT_VALID_1 = 3;
    public static final int AMOUNT_GREATER_THAN_0 = 4;
    public static final int AMOUNT_NOT_VALID = 5;
    public static final int AMOUNT_TWO_DECIMAL_PLACES = 6;
    public static final int AMOUNT_BETWEEN_0_AND_5000 = 7;
    public static final int PARTNER_SERVICE_FEE_NOT_VALID = 8;
    public static final int PARTNER_SERVICE_FEE_BETWEEN_0_AND_5000 = 9;
    public static final int CONSUMER_REFERENCE_NOT_SUPPLIED = 10;
    public static final int CONSUMER_REFERENCE_NOT_SUPPLIED_1 = 11;
    public static final int CONSUMER_REFERENCE_LENGTH = 12;
    public static final int CONSUMER_REFERENCE_LENGTH_1 = 13;
    public static final int CONSUMER_REFERENCE_LENGTH_2 = 14;
    public static final int PAYMENT_REFERENCE_NOT_SUPPLIED = 15;
    public static final int PAYMENT_REFERENCE_NOT_SUPPLIED_1 = 16;
    public static final int PAYMENT_REFERENCE_NOT_SUPPLIED_2 = 17;
    public static final int PAYMENT_REFERENCE_NOT_SUPPLIED_3 = 18;
    public static final int PAYMENT_REFERENCE_LENGTH = 19;
    public static final int PAYMENT_REFERENCE_LENGTH_1 = 20;
    public static final int PAYMENT_REFERENCE_LENGTH_2 = 21;
    public static final int PAYMENT_REFERENCE_LENGTH_3 = 22;
    public static final int PAYMENT_REFERENCE_LENGTH_4 = 23;
    public static final int CURRENCY_REQUIRED = 24;
    public static final int CURRENCY_LENGTH = 25;
    public static final int CURRENCY_NOT_SUPPORTED = 26;
    public static final int DEVICE_CATEGORY_UNKNOWN = 27;
    public static final int CARD_NUMBER_NOT_SUPPLIED = 28;
    public static final int TEST_CARDS_ONLY_IN_SANDBOX = 29;
    public static final int CARD_NUMBER_INVALID = 30;
    public static final int THREE_DIGIT_CV2_NOT_SUPPLIED = 31;
    public static final int FOUR_DIGIT_CV2_NOT_SUPPLIED = 32;
    public static final int CV2_NOT_VALID = 33;
    public static final int CV2_NOT_VALID_1 = 34;
    public static final int START_DATE_OR_ISSUE_NUMBER_MUST_BE_SUPPLIED = 35;
    public static final int START_DATE_NOT_SUPPLIED = 36;
    public static final int START_DATE_WRONG_LENGTH = 37;
    public static final int START_DATE_NOT_VALID = 38;
    public static final int START_DATE_NOT_VALID_FORMAT = 39;
    public static final int START_DATE_TOO_FAR_IN_PAST = 40;
    public static final int START_DATE_MONTH_OUTSIDE_EXPECTED_RANGE = 41;
    public static final int ISSUE_NUMBER_OUTSIDE_EXPECTED_RANGE = 42;
    public static final int EXPIRY_DATE_NOT_SUPPLIED = 43;
    public static final int EXPIRY_DATE_WRONG_LENGTH = 44;
    public static final int EXPIRY_DATE_NOT_VALID = 45;
    public static final int EXPIRY_DATE_IN_PAST = 46;
    public static final int EXPIRY_DATE_TOO_FAR_IN_FUTURE = 47;
    public static final int EXPIRY_DATE_MONTH_OUTSIDE_EXPECTED_RANGE = 48;
    public static final int POSTCODE_NOT_VALID = 49;
    public static final int POSTCODE_NOT_SUPPLIED = 50;
    public static final int POSTCODE_IS_INVALID = 51;
    public static final int CARD_TOKEN_NOT_SUPPLIED = 52;
    public static final int CARD_TOKEN_ORIGINAL_TRANSACTION_FAILED = 53;
    public static final int THREEDSECURE_PARES_NOT_SUPPLIED = 54;
    public static final int RECEIPT_ID_NOT_SUPPLIED = 55;
    public static final int RECEIPT_ID_IS_INVALID = 56;
    public static final int TRANSACTION_TYPE_IN_URL_INVALID = 57;
    public static final int PARTNER_APPLICATION_REFERENCE_NOT_SUPPLIED = 58;
    public static final int PARTNER_APPLICATION_REFERENCE_NOT_SUPPLIED_1 = 59;
    public static final int TYPE_OF_COMPANY_NOT_SUPPLIED = 60;
    public static final int TYPE_OF_COMPANY_UNKNOWN = 61;
    public static final int PRINCIPLE_NOT_SUPPLIED = 62;
    public static final int PRINCIPLE_SALUTATION_UNKNOWN = 63;
    public static final int PRINCIPLE_FIRST_NAME_NOT_SUPPLIED = 64;
    public static final int PRINCIPLE_FIRST_NAME_LENGTH = 65;
    public static final int PRINCIPLE_FIRST_NAME_NOT_SUPPLIED_1 = 66;
    public static final int PRINCIPLE_LAST_NAME_NOT_SUPPLIED = 67;
    public static final int PRINCIPLE_LAST_NAME_LENGTH = 68;
    public static final int PRINCIPLE_LAST_NAME_NOT_SUPPLIED_1 = 69;
    public static final int PRINCIPLE_EMAIL_OR_MOBILE_NOT_SUPPLIED = 70;
    public static final int PRINCIPLE_EMAIL_ADDRESS_NOT_SUPPLIED = 71;
    public static final int PRINCIPLE_EMAIL_ADDRESS_LENGTH = 72;
    public static final int PRINCIPLE_EMAIL_ADDRESS_NOT_VALID = 73;
    public static final int PRINCIPLE_EMAIL_ADDRESS_DOMAIN_NOT_VALID = 74;
    public static final int PRINCIPLE_MOBILE_OR_EMAIL_NOT_SUPPLIED = 75;
    public static final int PRINCIPLE_MOBILE_NUMBER_NOT_VALID = 76;
    public static final int PRINCIPLE_MOBILE_NUMBER_NOT_VALID_1 = 77;
    public static final int PRINCIPLE_MOBILE_NUMBER_LENGTH = 78;
    public static final int PRINCIPLE_HOME_PHONE_NOT_VALID = 79;
    public static final int PRINCIPLE_DATE_OF_BIRTH_NOT_SUPPLIED = 80;
    public static final int PRINCIPLE_DATE_OF_BIRTH_NOT_VALID = 81;
    public static final int PRINCIPLE_DATE_OF_BIRTH_AGE = 82;
    public static final int LOCATION_TRADING_NAME_NOT_SUPPLIED = 83;
    public static final int LOCATION_PARTNER_REFERENCE_NOT_SUPPLIED = 84;
    public static final int LOCATION_PARTNER_REFERENCE_NOT_SUPPLIED_1 = 85;
    public static final int LOCATION_PARTNER_REFERENCE_LENGTH = 86;
    public static final int FIRST_NAME_NOT_SUPPLIED = 87;
    public static final int FIRST_NAME_LENGTH = 88;
    public static final int LAST_NAME_NOT_SUPPLIED = 89;
    public static final int LAST_NAME_LENGTH = 90;
    public static final int EMAIL_ADDRESS_NOT_SUPPLIED = 91;
    public static final int EMAIL_ADDRESS_LENGTH = 92;
    public static final int EMAIL_ADDRESS_NOT_VALID = 93;
    public static final int EMAIL_ADDRESS_DOMAIN_NOT_VALID = 94;
    public static final int SCHEDULE_START_DATE_NOT_SUPPLIED = 95;
    public static final int SCHEDULE_START_DATE_FORMAT_NOT_VALID = 96;
    public static final int SCHEDULE_END_DATE_NOT_SUPPLIED = 97;
    public static final int SCHEDULE_END_DATE_FORMAT_NOT_VALID = 98;
    public static final int SCHEDULE_END_DATE_MUST_BE_GREATER_THAN_START_DATE = 99;
    public static final int SCHEDULE_REPEAT_NOT_SUPPLIED = 100;
    public static final int SCHEDULE_REPEAT_MUST_BE_GREATER_THAN_1 = 101;
    public static final int SCHEDULE_INTERVAL_NOT_VALID = 102;
    public static final int SCHEDULE_INTERVAL_MUST_BE_MINIMUM_5 = 103;
    public static final int ITEMSPERPAGE_NOT_SUPPLIED = 104;
    public static final int ITEMSPERPAGE_OUT_OF_RANGE = 105;
    public static final int PAGENUMBER_NOT_SUPPLIED = 106;
    public static final int PAGENUMBER_OUT_OF_RANGE = 107;
    public static final int LEGAL_NAME_NOT_SUPPLIED = 108;
    public static final int COMPANY_NUMBER_NOT_SUPPLIED = 109;
    public static final int COMPANY_NUMBER_WRONG_LENGTH = 110;
    public static final int CURRENT_ADDRESS_NOT_SUPPLIED = 111;
    public static final int BUILDING_NUMBER_OR_NAME_NOT_SUPPLIED = 112;
    public static final int BUILDING_NUMBER_OR_NAME_LENGTH = 113;
    public static final int ADDRESS_LINE1_NOT_SUPPLIED = 114;
    public static final int ADDRESS_LINE1_LENGTH = 115;
    public static final int SORTCODE_NOT_SUPPLIED = 116;
    public static final int SORTCODE_NOT_VALID = 117;
    public static final int ACCOUNT_NUMBER_NOT_SUPPLIED = 118;
    public static final int ACCOUNT_NUMBER_NOT_VALID = 119;
    public static final int LOCATION_TURNOVER_GREATER_THAN_0 = 120;
    public static final int AVERAGE_TRANSACTION_VALUE_NOT_SUPPLIED = 121;
    public static final int AVERAGE_TRANSACTION_VALUE_GREATER_THAN_0 = 122;
    public static final int AVERAGE_TRANSACTION_VALUE_GREATER_THAN_TURNOVER = 123;
    public static final int MCCCODE_NOT_SUPPLIED = 124;
    public static final int MCCCODE_UNKNOWN = 125;
    public static final int GENERIC_IS_INVALID = 200;
    public static final int GENERIC_HTML_INVALID = 210;

    public Integer getCode() {
        return code;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.code);
        dest.writeString(this.fieldName);
        dest.writeString(this.message);
        dest.writeString(this.detail);
    }

    protected ApiError(Parcel in) {
        this.code = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fieldName = in.readString();
        this.message = in.readString();
        this.detail = in.readString();
    }

    public static final Parcelable.Creator<ApiError> CREATOR = new Parcelable.Creator<ApiError>() {
        public ApiError createFromParcel(Parcel source) {
            return new ApiError(source);
        }

        public ApiError[] newArray(int size) {
            return new ApiError[size];
        }
    };

}