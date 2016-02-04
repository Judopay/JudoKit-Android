package com.judopay.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;

import com.judopay.Judo;

class AuthorizationEncoder {

    private final String apiToken;
    private final String apiSecret;

    AuthorizationEncoder(Context context) {
        this.apiToken = getApiToken(context);
        this.apiSecret = getApiSecret(context);
    }

    private static String getApiSecret(Context context) {
        return getManifestAttribute(context, "judo_api_secret", Judo.getApiSecret());
    }

    private static String getApiToken(Context context) {
        return getManifestAttribute(context, "judo_api_token", Judo.getApiToken());
    }

    private static String getManifestAttribute(Context context, String attribute, String defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();

            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            return bundle.getString(attribute);
        } catch (PackageManager.NameNotFoundException ignore) { }

        return defaultValue;
    }

    public String getAuthorization() {
        if (apiToken == null || apiToken.length() == 0) {
            throw new RuntimeException("Judo API Token not specified");
        }

        if (apiSecret == null || apiSecret.length() == 0) {
            throw new RuntimeException("Judo API Secret not specified");
        }

        String encoded = Base64.encodeToString(String.format("%s:%s", apiToken, apiSecret).getBytes(), Base64.NO_WRAP);
        return String.format("Basic %s", encoded);
    }

}
