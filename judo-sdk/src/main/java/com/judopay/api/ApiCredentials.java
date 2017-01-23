package com.judopay.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;

import com.judopay.Judo;
import com.judopay.error.TokenSecretError;

import static com.judopay.arch.TextUtil.isEmpty;

public class ApiCredentials {

    private static final String API_SECRET_MANIFEST_NAME = "judo_api_secret";
    private static final String API_TOKEN_MANIFEST_NAME = "judo_api_token";

    private final String apiToken;
    private final String apiSecret;
    private final String authorization;

    public ApiCredentials(String apiToken, String apiSecret) {
        this.apiToken = apiToken;
        this.apiSecret = apiSecret;
        this.authorization = getEncodedCredentials();
    }

    static ApiCredentials fromConfiguration(Context context, Judo judo) {
        return new ApiCredentials(getApiToken(context, judo), getApiSecret(context, judo));
    }

    private String getEncodedCredentials() {
        if (isEmpty(apiToken) || isEmpty(apiSecret)) {
            throw new TokenSecretError("API Token & Secret is not configured correctly, either:" +
                    "\t - Call to Judo.setup(\"token\", \"secret\", Judo.SANDBOX) in your Activity class" +
                    "\t - Add a meta-data attributes \"judo_api_token\" and \"judo_api_secret\" to your AndroidManifest.xml file");
        }
        return Base64.encodeToString(String.format("%s:%s", apiToken, apiSecret).getBytes(), Base64.NO_WRAP);
    }

    private String getAuthorization() {
        return authorization;
    }

    public String getBasicAuthorizationHeader() {
        return String.format("Basic %s", getAuthorization());
    }

    private static String getApiSecret(Context context, Judo judo) {
        return getManifestMetaData(context, API_SECRET_MANIFEST_NAME, judo.getApiSecret());
    }

    private static String getApiToken(Context context, Judo judo) {
        return getManifestMetaData(context, API_TOKEN_MANIFEST_NAME, judo.getApiToken());
    }

    private static String getManifestMetaData(Context context, String attribute, String defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();

            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            String metaData = bundle.getString(attribute);
            if (metaData != null && metaData.length() > 0) {
                return metaData;
            }
        } catch (NullPointerException | PackageManager.NameNotFoundException ignore) {
        }

        return defaultValue;
    }

}
