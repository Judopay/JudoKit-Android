package com.judopay;

import android.content.Context;
import android.content.SharedPreferences;

public class JudoPay {

    private static Context context;

    private static final String SHARED_PREFS = "JudoPrefs";
    private static final String API_TOKEN = "JudoApiToken";
    private static final String API_SECRET = "JudoApiSecret";

    public void initialise(Context context, String apiToken, String apiSecret) {
        JudoPay.context = context.getApplicationContext();
        saveApiCredentials(apiToken, apiSecret);
    }

    private void saveApiCredentials(String apiToken, String apiSecret) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        sharedPreferences.edit()
                .putString(API_TOKEN, apiToken)
                .putString(API_SECRET, apiSecret)
                .apply();
    }

    public static Context getContext() {
        return context;
    }

    public static String getBaseUrl() {
        return "https://partnerapi.judopay-sandbox.com";
    }
}
