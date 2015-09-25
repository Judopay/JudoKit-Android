package com.judopay.customer;

import com.google.gson.annotations.SerializedName;

public class Card {

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
}
