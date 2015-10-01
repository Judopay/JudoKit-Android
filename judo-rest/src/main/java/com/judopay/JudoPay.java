package com.judopay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class JudoPay {

    public static final int RESULT_PAYMENT_SUCCESS = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int RESULT_ERROR = 1;

    public static final String JUDO_PAYMENT = "Judo-Payment";
    public static final String JUDO_RECEIPT = "Judo-Receipt";

    private static final String API_HOST_SANDBOX = "https://partnerapi.judopay-sandbox.com";
    private static final String API_HOST_LIVE = "https://partnerapi.judopay.com";

    private static Context context;

    private static final String SHARED_PREFS = "JudoPrefs";

    private static final String SSL_PINNING_ENABLED = "JudoSslPinningEnabled";
    private static final String API_TOKEN = "JudoApiToken";
    private static final String API_SECRET = "JudoApiSecret";
    private static final String API_ENVIRONMENT = "JudoApiEnvironment";

    public void initialise(Context context,
                           String apiToken,
                           String apiSecret,
                           int apiEnvironment) {
        JudoPay.context = context.getApplicationContext();
        saveConfiguration(apiToken, apiSecret, apiEnvironment);
    }

    private void saveConfiguration(String apiToken, String apiSecret, int apiEnvironment) {
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

    public class Environment {
        public static final int LIVE = 0;
        public static final int SANDBOX = 1;
    }

}