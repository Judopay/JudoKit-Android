package com.judopay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class JudoPay {

    public static final int SUCCESS = Activity.RESULT_OK;
    public static final int ERROR = Activity.RESULT_CANCELED;

    public static final String API_HOST_SANDBOX = "https://partnerapi.judopay-sandbox.com";
    public static final String SSL_PINNING_ENABLED = "JudoSslPinningEnabled";

    private static Context context;

    private static final String SHARED_PREFS = "JudoPrefs";
    private static final String API_TOKEN = "JudoApiToken";
    private static final String API_SECRET = "JudoApiSecret";

    public void initialise(Context context, String apiToken, String apiSecret) {
        JudoPay.context = context.getApplicationContext();
        saveApiCredentials(apiToken, apiSecret);
    }

    private void saveApiCredentials(String apiToken, String apiSecret) {
        SharedPreferences sharedPreferences = getPreferences();

        sharedPreferences.edit()
                .putString(API_TOKEN, apiToken)
                .putString(API_SECRET, apiSecret)
                .apply();
    }

    public static Context getContext() {
        return context;
    }

    public static String getBaseUrl() {
        return API_HOST_SANDBOX;
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

}