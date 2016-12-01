package com.judopay.model;

import android.os.Parcel;

import com.judopay.api.Response;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The Receipt of a transaction performed with the judo API.
 */
@SuppressWarnings("unused")
public class Receipt extends Response {

    private Long judoID;
    private String receiptId;
    private String originalReceiptId;
    private String partnerServiceFee;
    private String yourPaymentReference;
    private String type;
    private Date createdAt;
    private String merchantName;
    private String appearsOnStatementAs;
    private BigDecimal originalAmount;
    private BigDecimal netAmount;
    private BigDecimal amount;
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

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public BigDecimal getAmount() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.judoID);
        dest.writeString(this.receiptId);
        dest.writeString(this.originalReceiptId);
        dest.writeString(this.partnerServiceFee);
        dest.writeString(this.yourPaymentReference);
        dest.writeString(this.type);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeString(this.merchantName);
        dest.writeString(this.appearsOnStatementAs);
        dest.writeSerializable(this.originalAmount);
        dest.writeSerializable(this.netAmount);
        dest.writeSerializable(this.amount);
        dest.writeString(this.currency);
        dest.writeParcelable(this.cardDetails, 0);
        dest.writeParcelable(this.consumer, 0);
        dest.writeParcelable(this.risks, 0);
        dest.writeString(this.md);
        dest.writeString(this.paReq);
        dest.writeString(this.acsUrl);
    }

    protected Receipt(Parcel in) {
        super(in);
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
        this.originalAmount = (BigDecimal) in.readSerializable();
        this.netAmount = (BigDecimal) in.readSerializable();
        this.amount = (BigDecimal) in.readSerializable();
        this.currency = in.readString();
        this.cardDetails = in.readParcelable(CardToken.class.getClassLoader());
        this.consumer = in.readParcelable(Consumer.class.getClassLoader());
        this.risks = in.readParcelable(Risks.class.getClassLoader());
        this.md = in.readString();
        this.paReq = in.readString();
        this.acsUrl = in.readString();
    }

    public static final Creator<Receipt> CREATOR = new Creator<Receipt>() {
        public Receipt createFromParcel(Parcel source) {
            return new Receipt(source);
        }

        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };
}