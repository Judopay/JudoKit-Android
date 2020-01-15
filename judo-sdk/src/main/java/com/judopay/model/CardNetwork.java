package com.judopay.model;

/**
 * The type of a payment card (e.g. Visa, Mastercard, American Express)
 * Can be detected from the card number digits using the {@link CardNetwork#fromCardNumber(String)} method.
 */
@SuppressWarnings("unused")
public final class CardNetwork {

    private static final String REGEX_VISA = "^4[0-9]{3}.*?";
    private static final String REGEX_MASTERCARD = "^5[1-5][0-9]{2}.*?";
    private static final String REGEX_MAESTRO = "^(5018|5020|5038|6304|6759|6761|6763|6334|6767|4903|4905|4911|4936|5641 82|6331 10|6333|6759|5600|5602|5603|5610|5611|5656|6700|6706|6773|6775|6709|6771|6773|6775).*?";
    private static final String REGEX_AMEX = "^3[47][0-9]{2}.*?";
    private static final String REGEX_DISCOVER = "^65.*?|64[4-9].*?|6011.*?|(622(1 2[6-9].*?|1 [3-9][0-9].*?|[2-8] [0-9][0-9].*?|9 [01][0-9].*?|9 2[0-5].*?).*?)";
    private static final String REGEX_DINERS_CLUB_INTERNATIONAL = "^(30[0-5]|309|36|38|39).*?";
    private static final String REGEX_JCB = "^(35[2-8][0-9]).*?";

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
    public static final int DISCOVER = 12;
    public static final int DINERS_CLUB_INTERNATIONAL = 13;

    public static int fromCardNumber(final String cardNumber) {
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

        if (cardNumber.matches(REGEX_DISCOVER)) {
            return DISCOVER;
        }

        if (cardNumber.matches(REGEX_DINERS_CLUB_INTERNATIONAL)) {
            return DINERS_CLUB_INTERNATIONAL;
        }

        if (cardNumber.matches(REGEX_JCB)) {
            return JCB;
        }
        return UNKNOWN;
    }

    public static String securityCodeHint(final int cardType) {
        if (CardNetwork.AMEX == cardType) {
            return "0000";
        } else {
            return "000";
        }
    }

    public static String securityCode(final int cardType) {
        switch (cardType) {
            case CardNetwork.AMEX:
                return "CID";
            case CardNetwork.VISA:
                return "CVV2";
            case CardNetwork.MASTERCARD:
                return "CVC2";
            case CardNetwork.CHINA_UNION_PAY:
                return "CVN2";
            case CardNetwork.JCB:
                return "CAV2";
            default:
                return "CVV";
        }
    }

    private static boolean startsWith(final String[] prefixes, final String cardNumber) {
        for (String prefix : prefixes) {
            if (cardNumber.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static int securityCodeLength(final int cardType) {
        if (cardType == CardNetwork.AMEX) {
            return 4;
        } else {
            return 3;
        }
    }
}