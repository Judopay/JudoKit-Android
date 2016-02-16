package com.judopay.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;

import com.judopay.Judo;
import com.judopay.exception.JudoInitializationException;

import static android.text.TextUtils.isEmpty;

class ApiCredentials {

    private static final String API_SECRET_MANIFEST_NAME = "judo_api_secret";
    private static final String API_TOKEN_MANIFEST_NAME = "judo_api_token";

    private final String apiToken;
    private final String apiSecret;
    private final String authorization;

    private ApiCredentials(String apiToken, String apiSecret) {
        this.apiToken = apiToken;
        this.apiSecret = apiSecret;
        this.authorization = getEncodedCredentials();
    }

    public static ApiCredentials fromConfiguration(Context context) {
        return new ApiCredentials(getApiToken(context), getApiSecret(context));
    }

    private String getEncodedCredentials() {
        if (isEmpty(apiToken)) {
            throw new JudoInitializationException("API token must be provided");
        }

        if (isEmpty(apiSecret)) {
            throw new JudoInitializationException("API secret must be provided");
        }

        return Base64.encodeToString(String.format("%s:%s", apiToken, apiSecret).getBytes(), Base64.NO_WRAP);
    }

    private String getAuthorization() {
        return authorization;
    }

    public String getBasicAuthorizationHeader() {
        return String.format("Basic %s", getAuthorization());
    }

    private static String getApiSecret(Context context) {
        return getManifestMetaData(context, API_SECRET_MANIFEST_NAME, Judo.getApiSecret());
    }

    private static String getApiToken(Context context) {
        return getManifestMetaData(context, API_TOKEN_MANIFEST_NAME, Judo.getApiToken());
    }

    private static String getManifestMetaData(Context context, String attribute, String defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();

            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            return bundle.getString(attribute);
        } catch (PackageManager.NameNotFoundException ignore) { }

        return defaultValue;
    }

}
