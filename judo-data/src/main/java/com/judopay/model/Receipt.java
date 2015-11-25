package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Receipt extends Response implements Parcelable {

    private long judoID;
    private String receiptId;
    private String originalReceiptId;
    private String partnerServiceFee;
    private String yourPaymentReference;
    private String type;
    private Date createdAt;
    private String message;
    private String merchantName;
    private String appearsOnStatementAs;
    private float originalAmount;
    private float netAmount;
    private float amount;
    private String currency;
    private CardToken cardDetails;
    private Consumer consumer;
    private Risks risks;

    private String md;
    private String paReq;
    private String acsUrl;

    public Receipt() { }

    public String getMd() {
        return md;
    }

    public String getPaReq() {
        return paReq;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getYourPaymentReference() {
        return yourPaymentReference;
    }

    public String getType() {
        return type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    public long getJudoID() {
        return judoID;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getAppearsOnStatementAs() {
        return appearsOnStatementAs;
    }

    public float getOriginalAmount() {
        return originalAmount;
    }

    public float getNetAmount() {
        return netAmount;
    }

    public float getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public CardToken getCardDetails() {
        return cardDetails;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public Risks getRisks() {
        return risks;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "judoID=" + judoID +
                ", receiptId='" + receiptId + '\'' +
                ", originalReceiptId='" + originalReceiptId + '\'' +
                ", partnerServiceFee='" + partnerServiceFee + '\'' +
                ", yourPaymentReference='" + yourPaymentReference + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", message='" + message + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", appearsOnStatementAs='" + appearsOnStatementAs + '\'' +
                ", originalAmount=" + originalAmount +
                ", netAmount=" + netAmount +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", cardDetails=" + cardDetails +
                ", consumer=" + consumer +
                ", risks=" + risks +
                ", md='" + md + '\'' +
                ", paReq='" + paReq + '\'' +
                ", acsUrl='" + acsUrl + '\'' +
                '}';
    }

    public boolean is3dSecureRequired() {
        return acsUrl != null && md != null && paReq != null;
    }

    public static class Builder {

        private Receipt receipt;

        public Builder() {
            this.receipt = new Receipt();
        }

        public Builder setJudoID(long judoID) {
            this.receipt.judoID = judoID;
            return this;
        }

        public Builder setReceiptId(String receiptId) {
            this.receipt.receiptId = receiptId;
            return this;
        }

        public Builder setOriginalReceiptId(String originalReceiptId) {
            this.receipt.originalReceiptId = originalReceiptId;
            return this;
        }

        public Builder setPartnerServiceFee(String partnerServiceFee) {
            this.receipt.partnerServiceFee = partnerServiceFee;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            this.receipt.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setType(String type) {
            this.receipt.type = type;
            return this;
        }

        public Builder setCreatedAt(Date createdAt) {
            this.receipt.createdAt = createdAt;
            return this;
        }

        public Builder setMessage(String message) {
            this.receipt.message = message;
            return this;
        }

        public Builder setMerchantName(String merchantName) {
            this.receipt.merchantName = merchantName;
            return this;
        }

        public Builder setAppearsOnStatementAs(String appearsOnStatementAs) {
            this.receipt.appearsOnStatementAs = appearsOnStatementAs;
            return this;
        }

        public Builder setOriginalAmount(float originalAmount) {
            this.receipt.originalAmount = originalAmount;
            return this;
        }

        public Builder setNetAmount(float netAmount) {
            this.receipt.netAmount = netAmount;
            return this;
        }

        public Builder setAmount(float amount) {
            this.receipt.amount = amount;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.receipt.currency = currency;
            return this;
        }

        public Builder setCardDetails(CardToken cardDetails) {
            this.receipt.cardDetails = cardDetails;
            return this;
        }

        public Builder setConsumer(Consumer consumer) {
            this.receipt.consumer = consumer;
            return this;
        }

        public Builder setRisks(Risks risks) {
            this.receipt.risks = risks;
            return this;
        }
        
        public Receipt build() {
            return receipt;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.judoID);
        dest.writeString(this.receiptId);
        dest.writeString(this.originalReceiptId);
        dest.writeString(this.partnerServiceFee);
        dest.writeString(this.yourPaymentReference);
        dest.writeString(this.type);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeString(this.message);
        dest.writeString(this.merchantName);
        dest.writeString(this.appearsOnStatementAs);
        dest.writeFloat(this.originalAmount);
        dest.writeFloat(this.netAmount);
        dest.writeFloat(this.amount);
        dest.writeString(this.currency);
        dest.writeParcelable(this.cardDetails, 0);
        dest.writeParcelable(this.consumer, 0);
        dest.writeParcelable(this.risks, 0);
        dest.writeString(this.md);
        dest.writeString(this.paReq);
        dest.writeString(this.acsUrl);
    }

    protected Receipt(Parcel in) {
        this.judoID = in.readLong();
        this.receiptId = in.readString();
        this.originalReceiptId = in.readString();
        this.partnerServiceFee = in.readString();
        this.yourPaymentReference = in.readString();
        this.type = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.message = in.readString();
        this.merchantName = in.readString();
        this.appearsOnStatementAs = in.readString();
        this.originalAmount = in.readFloat();
        this.netAmount = in.readFloat();
        this.amount = in.readFloat();
        this.currency = in.readString();
        this.cardDetails = in.readParcelable(CardToken.class.getClassLoader());
        this.consumer = in.readParcelable(Consumer.class.getClassLoader());
        this.risks = in.readParcelable(Risks.class.getClassLoader());
        this.md = in.readString();
        this.paReq = in.readString();
        this.acsUrl = in.readString();
    }

    public static final Parcelable.Creator<Receipt> CREATOR = new Parcelable.Creator<Receipt>() {
        public Receipt createFromParcel(Parcel source) {
            return new Receipt(source);
        }

        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };
}