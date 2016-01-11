package com.judopay.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;

class AuthorizationEncoder {

    private final String apiToken;
    private final String apiSecret;

    AuthorizationEncoder(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            apiToken = bundle.getString("judo_api_token");
            apiSecret = bundle.getString("judo_api_secret");
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
//            this.apiToken = JudoPay.getApiToken();
//            this.apiSecret = JudoPay.getApiSecret();
            throw new RuntimeException(e);
        }
    }

    public String getAuthorization() {
        if (apiToken == null || apiToken.length() == 0) {
            throw new RuntimeException("Judo Payments API Token not specified");
        }

        if (apiSecret == null || apiSecret.length() == 0) {
            throw new RuntimeException("Judo Payments API Secret not specified");
        }

        String encoded = Base64.encodeToString(String.format("%s:%s", apiToken, apiSecret).getBytes(), Base64.NO_WRAP);
        return String.format("Basic %s", encoded);
    }

}
