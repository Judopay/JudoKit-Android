package com.judopay.model;

public class Card {

    private String cardNumber;
    private Address cardAddress;
    private String expiryDate;
    private String startDate;
    private String issueNumber;
    private String cvv;

    public String getCardNumber() {
        return cardNumber;
    }

    public Address getCardAddress() {
        return cardAddress;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getCvv() {
        return cvv;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public String getCv2() {
        return cvv;
    }

    public boolean startDateAndIssueNumberRequired() {
        return CardType.MAESTRO == CardType.fromCardNumber(cardNumber);
    }

    public static class Builder {

        private final Card card;

        public Builder() {
            this.card = new Card();
        }

        public Builder setCardNumber(String cardNumber) {
            card.cardNumber = cardNumber;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            card.cardAddress = cardAddress;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            card.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
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
