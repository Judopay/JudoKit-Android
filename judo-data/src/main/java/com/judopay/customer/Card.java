package com.judopay.customer;

import java.util.Locale;

public class Card {

    private String cardNumber;
    private CardAddress cardAddress;
    private CardDate expiryDate;
    private CardDate startDate;
    private String issueNumber;
    private String cvv;

    public String getCardNumber() {
        return cardNumber;
    }

    public CardAddress getCardAddress() {
        return cardAddress;
    }

    public String getStartDate() {
        return formatDateString(startDate);
    }

    public String getExpiryDate() {
        return formatDateString(expiryDate);
    }

    private String formatDateString(CardDate date) {
        return String.format(Locale.ENGLISH, "%02d%02d", date.getMonth(), date.getYear());
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public String getCv2() {
        return cvv;
    }

    public boolean startDateAndIssueNumberRequired() {
        return CardType.MAESTRO == CardType.matchCardNumber(cardNumber);
    }

    public static class Builder {

        private Card card;

        public Builder() {
            this.card = new Card();
        }

        public Builder setCardNumber(String cardNumber) {
            card.cardNumber = cardNumber;
            return this;
        }

        public Builder setCardAddress(CardAddress cardAddress) {
            card.cardAddress = cardAddress;
            return this;
        }

        public Builder setExpiryDate(CardDate expiryDate) {
            card.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(CardDate startDate) {
            card.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            card.issueNumber = issueNumber;
            return this;
        }

        public Builder setCvv(String cvv) {
            card.cvv = cvv;
            return this;
        }

        public Card build() {
            return card;
        }

    }
}
