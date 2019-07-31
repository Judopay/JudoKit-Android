package com.judopay.model;


import com.google.gson.annotations.SerializedName;

public class Browser {
    @SerializedName("AcceptHeader")
    private final String acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    @SerializedName("JavaEnabled")
    private final String javaEnabled = "false";
    @SerializedName("JavascriptEnabled")
    private final String javaScriptEnabled= "true";
    @SerializedName("Language")
    private final String language;
    @SerializedName("ColorDepth")
    private final String colorDepth = "32";
    @SerializedName("ScreenHeight")
    private final String screenHeight;
    @SerializedName("ScreenWidth")
    private final String screeWidth;
    @SerializedName("TimeZone")
    private final String timeZone;
    @SerializedName("UserAgent")
    private final String userAgent;

    public Browser(final String language, final String screenHeight, final String screeWidth, final String timeZone, final String userAgent) {
        this.language = language;
        this.screenHeight = screenHeight;
        this.screeWidth = screeWidth;
        this.timeZone = timeZone;
        this.userAgent = userAgent;
    }
}