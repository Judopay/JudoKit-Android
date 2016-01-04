package com.judopay.model;

public class CardType {

    private static final String REGEX_VISA = "^4[0-9]{3}.*?";
    private static final String REGEX_MASTERCARD = "^5[1-5][0-9]{2}.*?";
    private static final String REGEX_MAESTRO = "^(5018|5020|5038|6304|6759|6761|6763|6334|6767|4903|4905|4911|4936|564182|633110|6333|6759|5600|5602|5603|5610|5611|5656|6700|6706|6773|6775|6709|6771|6773|6775).*?";
    private static final String REGEX_AMEX = "^3[47][0-9]{2}.*?";

    private static final String[] AMEX_PREFIXES = {"34", "37"};
    private static final String[] VISA_PREFIXES = {"4"};
    private static final String[] MASTERCARD_PREFIXES = {"50", "51", "52", "53", "54", "55"};

    public static final int UNKNOWN = 0;
    public static final int VISA = 1;
    public static final int MASTERCARD = 2;
    public static final int VISA_ELECTRON = 3;
    public static final int SWITCH = 4;
    public static final int SOLO = 5;
    public static final int LASER = 6;
    public static final int CHINA_UNION_PAY = 7;
    public static final int AMEX = 8;
    public static final int JCB = 9;
    public static final int MAESTRO = 10;
    public static final int VISA_DEBIT = 11;

    public static final String VISA_PATTERN = "0000 0000 0000 0000";
    public static final String AMEX_PATTERN = "0000 000000 00000";

    public static int matchCardNumber(String cardNumber) {
        if (startsWith(VISA_PREFIXES, cardNumber) || cardNumber.matches(REGEX_VISA)) {
            return VISA;
        }

        if (startsWith(MASTERCARD_PREFIXES, cardNumber) || cardNumber.matches(REGEX_MASTERCARD)) {
            return MASTERCARD;
        }

        if (cardNumber.matches(REGEX_MAESTRO)) {
            return MAESTRO;
        }

        if (startsWith(AMEX_PREFIXES, cardNumber) || cardNumber.matches(REGEX_AMEX)) {
            return AMEX;
        }
        return UNKNOWN;
    }

    private static boolean startsWith(String[] prefixes, String cardNumber) {
        for (String prefix : prefixes) {
            if (cardNumber.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}