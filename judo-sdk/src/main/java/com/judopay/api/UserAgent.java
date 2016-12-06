package com.judopay.api;

import java.util.Locale;

class UserAgent {

    private final String sdkVersion;
    private final String androidVersion;
    private final String manufacturer;
    private final String model;
    private final String locale;

    UserAgent(String locale) {
        this.sdkVersion = com.judopay.BuildConfig.VERSION_NAME;
        this.androidVersion = android.os.Build.VERSION.RELEASE;
        this.manufacturer = android.os.Build.MANUFACTURER;
        this.model = android.os.Build.MODEL;
        this.locale = locale;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "JudoPaymentsSDK/%s (android %s %s %s; %s)", sdkVersion, androidVersion, manufacturer, model, locale);
    }

}
