package com.judopay;

import android.content.Context;

public class JudoPay {

    private static Context context;

    public void initialise(Context context, String apiToken, String apiSecret) {
        context = context.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static String getBaseUrl() {
        return "https://partnerapi.judopay-sandbox.com";
    }
}
