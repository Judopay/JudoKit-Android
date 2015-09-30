package com.judopay.customer;

import com.google.gson.annotations.SerializedName;

public class CardSummary {

    private String endDate;

    @SerializedName("cardLastfour")
    private String lastFour;

    @SerializedName("cardToken")
    private String token;

    @SerializedName("cardType")
    private long type;

    public String getLastFour() {
        return lastFour;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getToken() {
        return token;
    }

    public long getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CardSummary{" +
                "endDate='" + endDate + '\'' +
                ", lastFour='" + lastFour + '\'' +
                ", token='" + token + '\'' +
                ", type=" + type +
                '}';
    }

}
