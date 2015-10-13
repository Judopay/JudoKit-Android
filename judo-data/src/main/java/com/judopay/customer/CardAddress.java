package com.judopay.customer;

public class CardAddress {

    private int countryCode;
    private String postcode;

    public String getPostcode() {
        return postcode;
    }

    public int getCountryCode() {
        return countryCode;
    }

    private CardAddress() { }

    public static class Builder {

        private CardAddress cardAddress;

        public Builder() {
            this.cardAddress = new CardAddress();
        }

        public Builder setCountryCode(int countryCode) {
            cardAddress.countryCode = countryCode;
            return this;
        }

        public Builder setPostcode(String postcode) {
            cardAddress.postcode = postcode;
            return this;
        }

        public CardAddress build() {
            return cardAddress;
        }
    }
}