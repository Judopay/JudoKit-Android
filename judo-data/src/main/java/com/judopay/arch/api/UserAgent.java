package com.judopay.arch.api;

import java.util.Locale;

class UserAgent {

    private final String sdkVersion;
    private final String androidVersion;
    private final String manufacturer;
    private final String model;
    private final String locale;

    public UserAgent(String sdkVersion, String androidVersion, String manufacturer, String model, String locale) {
        this.sdkVersion = sdkVersion;
        this.androidVersion = androidVersion;
        this.manufacturer = manufacturer;
        this.model = model;
        this.locale = locale;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "JudoPaymentsSDK/%s (android %s %s %s; %s)", sdkVersion, androidVersion, manufacturer, model, locale);
    }

}
