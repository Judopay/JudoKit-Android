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
    private String securityCode;

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

    public String getSecurityCode() {
        return securityCode;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public boolean startDateAndIssueNumberRequired() {
        return CardNetwork.MAESTRO == CardNetwork.fromCardNumber(cardNumber);
    }

    public static class Builder {

        private String cardNumber;
        private Address cardAddress;
        private String expiryDate;
        private String startDate;
        private String issueNumber;
        private String securityCode;

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber.replaceAll("\\s+", "");
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

        public Builder setSecurityCode(String securityCOde) {
            this.securityCode = securityCOde;
            return this;
        }

        public Card build() {
            Card card = new Card();

            card.cardNumber = cardNumber;
            card.cardAddress = cardAddress;
            card.expiryDate = expiryDate;
            card.startDate = startDate;
            card.issueNumber = issueNumber;
            card.securityCode = securityCode;

            return card;
        }
    }
}
