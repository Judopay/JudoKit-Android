package com.judopay.model;

/**
 * Represents the card data entered by the user.
 * Use {@link Card.Builder} to construct an instance.
 */
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

        private String cardNumber;
        private Address cardAddress;
        private String expiryDate;
        private String startDate;
        private String issueNumber;
        private String cvv;

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setCardAddress(Address cardAddress) {
            this.cardAddress = cardAddress;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setIssueNumber(String issueNumber) {
            this.issueNumber = issueNumber;
            return this;
        }

        public Builder setCvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public Card build() {
            Card card = new Card();

            card.cardNumber = cardNumber;
            card.cardAddress = cardAddress;
            card.expiryDate = expiryDate;
            card.startDate = startDate;
            card.issueNumber = issueNumber;
            card.cvv = cvv;

            return card;
        }
    }
}
