package com.judopay.customer;

public class Card {

    private String cardNumber;
    private CardAddress cardAddress;
    private CardDate expiryDate;
    private CardDate startDate;
    private String issueNumber;

    public Card(String cardNumber, CardDate startDate, CardDate expiryDate) {
        this.cardNumber = cardNumber;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public CardAddress getCardAddress() {
        return cardAddress;
    }

    public CardDate getStartDate() {
        return startDate;
    }

    public CardDate getExpiryDate() {
        return expiryDate;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public boolean isLuhnValid() {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    public boolean isStartDateValid() {
        return startDate.isPastDate();
    }

    public boolean isExpiryDateValid() {
        return !expiryDate.isPastDate();
    }

    public int getType() {
        if (cardNumber.matches(Type.REGEX_VISA)) {
            return Type.VISA;
        }
        if (cardNumber.matches(Type.REGEX_MC)) {
            return Type.MASTERCARD;
        }
        if (cardNumber.matches(Type.REGEX_MAESTRO)) {
            return Type.MAESTRO;
        }
        if (cardNumber.matches(Type.REGEX_AMEX)) {
            return Type.AMEX;
        }
        return Type.UNKNOWN;
    }

    public static class Type {

        private static final String REGEX_VISA = "^4[0-9]{3}.*?";
        private static final String REGEX_MC = "^5[1-5][0-9]{2}.*?";
        private static final String REGEX_MAESTRO = "^(5018|5020|5038|6304|6759|6761|6763|6334|6767|4903|4905|4911|4936|564182|633110|6333|6759|5600|5602|5603|5610|5611|5656|6700|6706|6773|6775|6709|6771|6773|6775).*?";
        private static final String REGEX_AMEX = "^3[47][0-9]{2}.*?";
        private static final String REGEX_DISCOVER = "^6(?:011|5[0-9]{2})$";
        private static final String REGEX_DINERS = "^3(?:0[0-5]|[68][0-9])[0-9]$";
        
        public static final int UNKNOWN=0;
        public static final int VISA=1;
        public static final int MASTERCARD=2;
        public static final int VISA_ELECTRON=3;
        public static final int SWITCH=4;
        public static final int SOLO=5;
        public static final int LASER=6;
        public static final int CHINA_UNION_PAY=7;
        public static final int AMEX=8;
        public static final int JCB=9;
        public static final int MAESTRO=10;
        public static final int VISA_DEBIT=11;
    }

}
