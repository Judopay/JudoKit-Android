package com.judopay.api;

import android.util.Base64;

import com.judopay.Judo;
import com.judopay.JudoApi;

class AuthorizationEncoder {

    public String getAuthorization() {
        JudoApi api = Judo.getApi();

        String apiToken = api.getApiToken();
        String apiSecret = api.getApiSecret();

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
