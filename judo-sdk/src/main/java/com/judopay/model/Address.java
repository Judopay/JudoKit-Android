package com.judopay.model;

/**
 * An Address linked to a payment card, used when address verification is performed.
 */
public class Address {

    private long countryCode;
    private String postCode;

    public long getCountryCode() {
        return countryCode;
    }

    public String getPostCode() {
        return postCode;
    }

    public static class Builder {

        private final Address address;

        public Builder() {
            this.address = new Address();
        }

        public Builder setCountryCode(long countryCode) {
            address.countryCode = countryCode;
            return this;
        }

        public Builder setPostCode(String postCode) {
            address.postCode = postCode;
            return this;
        }

        public Address build() {
            return address;
        }
    }

}