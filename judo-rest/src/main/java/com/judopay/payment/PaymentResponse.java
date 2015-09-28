package com.judopay.payment;

import com.judopay.customer.Card;
import com.judopay.Consumer;

import java.util.Date;

public class PaymentResponse {

    private String receiptId;
    private String yourPaymentReference;
    private String type;
    private Date createdAt;
    private String result;
    private String message;
    private long judoId;
    private String merchantName;
    private String appearsOnStatementAs;
    private String originalAmount;
    private String netAmount;
    private String amount;
    private String currency;
    private Card cardDetails;
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

    public long getJudoId() {
        return judoId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getAppearsOnStatementAs() {
        return appearsOnStatementAs;
    }

    public String getOriginalAmount() {
        return originalAmount;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Card getCardDetails() {
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
                "risks=" + risks +
                ", consumer=" + consumer +
                ", cardDetails=" + cardDetails +
                ", currency='" + currency + '\'' +
                ", amount='" + amount + '\'' +
                ", netAmount='" + netAmount + '\'' +
                ", originalAmount='" + originalAmount + '\'' +
                ", appearsOnStatementAs='" + appearsOnStatementAs + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", judoId=" + judoId +
                ", message='" + message + '\'' +
                ", result='" + result + '\'' +
                ", createdAt=" + createdAt +
                ", type='" + type + '\'' +
                ", yourPaymentReference='" + yourPaymentReference + '\'' +
                ", receiptId='" + receiptId + '\'' +
                '}';
    }
}
