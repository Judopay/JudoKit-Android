package com.judopay.model;

import com.google.gson.annotations.SerializedName;

public class ThreeDSecure {
    @SerializedName("Browser")
    private final Browser browser;

    public ThreeDSecure(final Browser browser) {
        this.browser = browser;
    }
}
