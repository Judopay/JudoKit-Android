package com.judopay;

import android.app.Activity;
import android.content.Context;

public class JudoPay {

    public static final int RESULT_PAYMENT_SUCCESS = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;

    public static final int RESULT_PAYMENT_DECLINED = 2;
    public static final int RESULT_ERROR = 1;

    public static final String JUDO_RECEIPT = "Judo-Receipt";
    public static final String EXTRA_PAYMENT = "Judo-Payment";

    private static final String API_HOST_SANDBOX = "https://gw1.judopay-sandbox.com";
    private static final String API_HOST_LIVE = "https://gw1.judopay.com";

    private static Context context;
    private static JudoPayApi api;

    public static void setup(Context context, String apiToken, String apiSecret, int apiEnvironment) {
        JudoPay.context = context.getApplicationContext();
        api = new JudoPayApi(apiToken, apiSecret, apiEnvironment);
    }

    public static Context getContext() {
        return context;
    }

    public static String getApiEnvironmentHost() {
        return api.getApiEnvironment() == Environment.SANDBOX ? API_HOST_SANDBOX : API_HOST_LIVE;
    }

    public static boolean isSslPinningEnabled() {
        return api.isSslPinningEnabled();
    }

    public static void setSslPinningEnabled(boolean enabled) {
        api.setSslPinningEnabled(enabled);
    }

    public static void setAvsEnabled(boolean enabled) {
        api.setAvsEnabled(enabled);
    }

    public static boolean isAvsEnabled() {
        return api.isAvsEnabled();
    }

    public class Environment {
        public static final int LIVE = 0;
        public static final int SANDBOX = 1;
    }

}