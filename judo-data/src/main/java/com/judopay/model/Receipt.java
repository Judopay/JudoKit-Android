package com.judopay.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.api.Response;

import java.util.Date;

public class Receipt extends Response implements Parcelable {

    private Long judoID;
    private String receiptId;
    private String originalReceiptId;
    private String partnerServiceFee;
    private String yourPaymentReference;
    private String type;
    private Date createdAt;
    private String merchantName;
    private String appearsOnStatementAs;
    private Float originalAmount;
    private Float netAmount;
    private Float amount;
    private String currency;
    private CardToken cardDetails;
    private Consumer consumer;
    private Risks risks;

    private String md;
    private String paReq;
    private String acsUrl;

    public Receipt() { }

    public Long getJudoID() {
        return judoID;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getOriginalReceiptId() {
        return originalReceiptId;
    }

    public String getPartnerServiceFee() {
        return partnerServiceFee;
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

    public String getMerchantName() {
        return merchantName;
    }

    public String getAppearsOnStatementAs() {
        return appearsOnStatementAs;
    }

    public Float getOriginalAmount() {
        return originalAmount;
    }

    public Float getNetAmount() {
        return netAmount;
    }

    public Float getAmount() {
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

    public String getMd() {
        return md;
    }

    public String getPaReq() {
        return paReq;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "acsUrl='" + acsUrl + '\'' +
                ", judoID=" + judoID +
                ", receiptId='" + receiptId + '\'' +
                ", originalReceiptId='" + originalReceiptId + '\'' +
                ", partnerServiceFee='" + partnerServiceFee + '\'' +
                ", yourPaymentReference='" + yourPaymentReference + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
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
                '}';
    }


    public boolean is3dSecureRequired() {
        return acsUrl != null && md != null && paReq != null;
    }

    public static class Builder {

        private long judoID;
        private String receiptId;
        private String originalReceiptId;
        private String partnerServiceFee;
        private String yourPaymentReference;
        private String type;
        private Date createdAt;
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

        public Builder() { }

        public Builder setJudoID(long judoID) {
            this.judoID = judoID;
            return this;
        }

        public Builder setReceiptId(String receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder setOriginalReceiptId(String originalReceiptId) {
            this.originalReceiptId = originalReceiptId;
            return this;
        }

        public Builder setPartnerServiceFee(String partnerServiceFee) {
            this.partnerServiceFee = partnerServiceFee;
            return this;
        }

        public Builder setYourPaymentReference(String yourPaymentReference) {
            this.yourPaymentReference = yourPaymentReference;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setMerchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public Builder setAppearsOnStatementAs(String appearsOnStatementAs) {
            this.appearsOnStatementAs = appearsOnStatementAs;
            return this;
        }

        public Builder setOriginalAmount(float originalAmount) {
            this.originalAmount = originalAmount;
            return this;
        }

        public Builder setNetAmount(float netAmount) {
            this.netAmount = netAmount;
            return this;
        }

        public Builder setAmount(float amount) {
            this.amount = amount;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setCardDetails(CardToken cardDetails) {
            this.cardDetails = cardDetails;
            return this;
        }

        public Builder setConsumer(Consumer consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder setRisks(Risks risks) {
            this.risks = risks;
            return this;
        }
        
        public Receipt build() {
            Receipt receipt = new Receipt();

            receipt.judoID = judoID;
            receipt.receiptId = receiptId;
            receipt.originalReceiptId = originalReceiptId;
            receipt.partnerServiceFee = partnerServiceFee;
            receipt.yourPaymentReference = yourPaymentReference;
            receipt.type = type;
            receipt.createdAt = createdAt;
            receipt.merchantName = merchantName;
            receipt.appearsOnStatementAs = appearsOnStatementAs;
            receipt.originalAmount = originalAmount;
            receipt.netAmount = netAmount;
            receipt.amount = amount;
            receipt.currency = currency;
            receipt.cardDetails = cardDetails;
            receipt.consumer = consumer;
            receipt.risks = risks;
            receipt.md = md;
            receipt.paReq = paReq;
            receipt.acsUrl = acsUrl;

            return receipt;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.judoID);
        dest.writeString(this.receiptId);
        dest.writeString(this.originalReceiptId);
        dest.writeString(this.partnerServiceFee);
        dest.writeString(this.yourPaymentReference);
        dest.writeString(this.type);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeString(this.merchantName);
        dest.writeString(this.appearsOnStatementAs);
        dest.writeValue(this.originalAmount);
        dest.writeValue(this.netAmount);
        dest.writeValue(this.amount);
        dest.writeString(this.currency);
        dest.writeParcelable(this.cardDetails, 0);
        dest.writeParcelable(this.consumer, 0);
        dest.writeParcelable(this.risks, 0);
        dest.writeString(this.md);
        dest.writeString(this.paReq);
        dest.writeString(this.acsUrl);
    }

    protected Receipt(Parcel in) {
        this.judoID = (Long) in.readValue(Long.class.getClassLoader());
        this.receiptId = in.readString();
        this.originalReceiptId = in.readString();
        this.partnerServiceFee = in.readString();
        this.yourPaymentReference = in.readString();
        this.type = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.merchantName = in.readString();
        this.appearsOnStatementAs = in.readString();
        this.originalAmount = (Float) in.readValue(Float.class.getClassLoader());
        this.netAmount = (Float) in.readValue(Float.class.getClassLoader());
        this.amount = (Float) in.readValue(Float.class.getClassLoader());
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