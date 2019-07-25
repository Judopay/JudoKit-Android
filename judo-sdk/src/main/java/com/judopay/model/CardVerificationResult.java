package com.judopay.model;

import com.google.gson.annotations.SerializedName;

/**
 * The result from a 3D-Secure verification, containing the data required to complete the
 * transaction with the judo API.
 */
public class CardVerificationResult {

    @SerializedName("MD")
    private final String md;

    @SerializedName("PaRes")
    private final String paRes;

    public CardVerificationResult(final String md, final String paRes) {
        this.md = md;
        this.paRes = paRes;
    }

    public String getMd() {
        return md;
    }

    public String getPaRes() {
        return paRes;
    }
}
