package com.judopay.payment;

import android.os.Parcel;
import android.os.Parcelable;

import com.judopay.Consumer;
import com.judopay.arch.api.Response;
import com.judopay.customer.CardSummary;

import java.util.Date;

public class PaymentResponse extends Response implements Parcelable {

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
    private CardSummary cardDetails;
    private Consumer consumer;
    private Risks risks;

    public PaymentResponse() { }

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

    public CardSummary getCardDetails() {
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
        return "PaymentResponse{" +
                "judoID=" + judoID +
                ", receiptId='" + receiptId + '\'' +
                ", originalReceiptId='" + originalReceiptId + '\'' +
                ", partnerServiceFee='" + partnerServiceFee + '\'' +
                ", yourPaymentReference='" + yourPaymentReference + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", result='" + result + '\'' +
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
                '}';
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
        dest.writeString(this.result);
        dest.writeString(this.message);
        dest.writeString(this.merchantName);
        dest.writeString(this.appearsOnStatementAs);
        dest.writeFloat(this.originalAmount);
        dest.writeFloat(this.netAmount);
        dest.writeFloat(this.amount);
        dest.writeString(this.currency);
        dest.writeParcelable(this.cardDetails, flags);
        dest.writeParcelable(this.consumer, 0);
        dest.writeParcelable(this.risks, flags);
    }

    private PaymentResponse(Parcel in) {
        this.judoID = in.readLong();
        this.receiptId = in.readString();
        this.originalReceiptId = in.readString();
        this.partnerServiceFee = in.readString();
        this.yourPaymentReference = in.readString();
        this.type = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.result = in.readString();
        this.message = in.readString();
        this.merchantName = in.readString();
        this.appearsOnStatementAs = in.readString();
        this.originalAmount = in.readFloat();
        this.netAmount = in.readFloat();
        this.amount = in.readFloat();
        this.currency = in.readString();
        this.cardDetails = in.readParcelable(CardSummary.class.getClassLoader());
        this.consumer = in.readParcelable(Consumer.class.getClassLoader());
        this.risks = in.readParcelable(Risks.class.getClassLoader());
    }

    public static final Parcelable.Creator<PaymentResponse> CREATOR = new Parcelable.Creator<PaymentResponse>() {
        public PaymentResponse createFromParcel(Parcel source) {
            return new PaymentResponse(source);
        }

        public PaymentResponse[] newArray(int size) {
            return new PaymentResponse[size];
        }
    };

}