package com.judopay.model;

import com.google.gson.annotations.SerializedName;

public class ThreeDSecureInfo {

    @SerializedName("MD")
    private final String md;

    @SerializedName("PaRes")
    private final String paRes;

    public ThreeDSecureInfo(String md, String paRes) {
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
