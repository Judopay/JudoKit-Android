package com.judopay.payment;

import com.judopay.Consumer;
import com.judopay.customer.CardSummary;

import java.util.Date;

public class PaymentResponse {

    private long judoID;
    private String receiptId;
    private String originalReceiptId;
    private String partnerServiceFee;
    private String yourPaymentReference;
    private String type;
    private Date createdAt;
    private String result;
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

    public String getResult() {
        return result;
    }

    public boolean isSuccess() {
        return "Success".equals(result);
    }

    public boolean isDeclined() {
        return "Declined".equals(result);
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
}
