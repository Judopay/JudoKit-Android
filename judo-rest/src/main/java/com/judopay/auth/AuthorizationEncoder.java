package com.judopay.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class AuthorizationEncoder {

    public static final String SHARED_PREFS = "JudoPrefs";
    public static final String API_TOKEN_SHARED_PREF = "JudoApiToken";
    public static final String API_SECRET_SHARED_PREF = "JudoApiSecret";

    private SharedPreferences sharedPreferences;

    public AuthorizationEncoder(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public String getAuthorization() {
        String apiToken = sharedPreferences.getString(API_TOKEN_SHARED_PREF, null);
        String apiSecret = sharedPreferences.getString(API_SECRET_SHARED_PREF, null);

        if (apiToken == null || apiToken.length() == 0) {
            throw new RuntimeException("Judo Payments API Token not specified");
        }

        if (apiSecret == null || apiSecret.length() == 0) {
            throw new RuntimeException("Judo Payments API Secret not specified");
        }

        String encoded = Base64.encodeToString(String.format("%s:%s", apiToken, apiSecret).getBytes(), Base64.DEFAULT);
        return String.format("Basic %s", encoded);
    }

}
