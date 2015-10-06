package com.judopay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class JudoPay {

    public static final int RESULT_PAYMENT_SUCCESS = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int RESULT_PAYMENT_DECLINED = 2;
    public static final int RESULT_ERROR = 1;

    public static final String JUDO_RECEIPT = "Judo-Receipt";
    public static final String EXTRA_PAYMENT = "Judo-Payment";

    private static final String API_HOST_SANDBOX = "https://partnerapi.judopay-sandbox.com";
    private static final String API_HOST_LIVE = "https://partnerapi.judopay.com";

    private static Context context;

    private static final String SHARED_PREFS = "JudoPrefs";

    private static final String AVS_ENABLED = "JudoAvsEnabled";
    private static final String SSL_PINNING_ENABLED = "JudoSslPinningEnabled";
    private static final String API_TOKEN = "JudoApiToken";
    private static final String API_SECRET = "JudoApiSecret";
    private static final String API_ENVIRONMENT = "JudoApiEnvironment";

    public static void setup(Context context,
                             String apiToken,
                             String apiSecret,
                             int apiEnvironment) {
        JudoPay.context = context.getApplicationContext();
        saveConfiguration(apiToken, apiSecret, apiEnvironment);
    }

    private static void saveConfiguration(String apiToken, String apiSecret, int apiEnvironment) {
        SharedPreferences sharedPreferences = getPreferences();

        sharedPreferences.edit()
                .putString(API_TOKEN, apiToken)
                .putString(API_SECRET, apiSecret)
                .putInt(API_ENVIRONMENT, apiEnvironment)
                .apply();
    }

    public static Context getContext() {
        return context;
    }

    public static String getApiEnvironmentHost() {
        int environment = getPreferences().getInt(API_ENVIRONMENT, Environment.LIVE);

        return environment == Environment.SANDBOX ? API_HOST_SANDBOX : API_HOST_LIVE;
    }

    public static boolean isSslPinningEnabled() {
        return getPreferences().getBoolean(SSL_PINNING_ENABLED, true);
    }

    public static void setSslPinningEnabled(boolean enabled) {
        getPreferences()
                .edit()
                .putBoolean(SSL_PINNING_ENABLED, enabled)
                .commit();
    }

    private static SharedPreferences getPreferences() {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public static void setAvsEnabled(boolean enabled) {
        getPreferences()
                .edit()
                .putBoolean(AVS_ENABLED, enabled)
                .commit();
    }

    public static boolean isAvsEnabled() {
        return getPreferences()
                .getBoolean(AVS_ENABLED, false);
    }

    public class Environment {
        public static final int LIVE = 0;
        public static final int SANDBOX = 1;
    }

}