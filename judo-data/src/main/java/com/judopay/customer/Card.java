package com.judopay.customer;

import java.util.Locale;

public class Card {

    private String cardNumber;
    private CardAddress cardAddress;
    private CardDate expiryDate;
    private CardDate startDate;
    private String issueNumber;
    private String cvv;

    public Card(String cardNumber, CardAddress cardAddress, CardDate startDate, CardDate expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.cardAddress = cardAddress;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
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

    public String getExpiryDate() {
        return String.format(Locale.ENGLISH, "%02d%02d", expiryDate.getMonth(), expiryDate.getYear());
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
        return CardType.matchCardNumber(cardNumber);
    }

    public boolean isCvvValid() {
        try {
            switch (getType()) {
                case CardType.AMEX:
                    return isCidvValid();
                default:
                    return isCv2Valid();
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isCv2Valid() {
        int cv2 = Integer.parseInt(cvv);
        return (cv2 >= 0 && cv2 < 1000);
    }

    private boolean isCidvValid() {
        int cidv = Integer.parseInt(cvv);
        return (cidv >= 0 && cidv < 10000);
    }

    public String getCv2() {
        return cvv;
    }

}
