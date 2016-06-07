package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the card data entered by the user.
 * Use {@link Card.Builder} to construct an instance.
 */
public final class Card implements Parcelable {

    private String cardNumber;
    private Address address;
    private String expiryDate;
    private String startDate;
    private String issueNumber;
    private String securityCode;

    public String getCardNumber() {
        return cardNumber;
    }

    public Address getAddress() {
        return address;
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

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        private String cardNumber;
        private Address address;
        private String expiryDate;
        private String startDate;
        private String issueNumber;
        private String securityCode;

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber.replaceAll("\\s+", "");
            return this;
        }

        public Builder setAddress(Address address) {
            this.address = address;
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
            card.address = address;
            card.expiryDate = expiryDate;
            card.startDate = startDate;
            card.issueNumber = issueNumber;
            card.securityCode = securityCode;

            return card;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardNumber);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.expiryDate);
        dest.writeString(this.startDate);
        dest.writeString(this.issueNumber);
        dest.writeString(this.securityCode);
    }

    public Card() {
    }

    protected Card(Parcel in) {
        this.cardNumber = in.readString();
        this.address = in.readParcelable(Address.class.getClassLoader());
        this.expiryDate = in.readString();
        this.startDate = in.readString();
        this.issueNumber = in.readString();
        this.securityCode = in.readString();
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}