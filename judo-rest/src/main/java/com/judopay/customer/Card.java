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
}
