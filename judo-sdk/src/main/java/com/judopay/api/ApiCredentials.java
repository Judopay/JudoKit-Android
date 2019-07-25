package com.judopay.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;

import com.judopay.Judo;
import com.judopay.error.TokenSecretError;

import java.nio.charset.StandardCharsets;

import static com.judopay.arch.TextUtil.isEmpty;

public class ApiCredentials {

    private static final String API_SECRET_MANIFEST_NAME = "judo_api_secret";
    private static final String API_TOKEN_MANIFEST_NAME = "judo_api_token";

    private final String apiToken;
    private final String apiSecret;
    private final String authorization;

    public ApiCredentials(final String apiToken, final String apiSecret) {
        this.apiToken = apiToken;
        this.apiSecret = apiSecret;
        this.authorization = getEncodedCredentials();
    }

    static ApiCredentials fromConfiguration(final Context context, final Judo judo) {
        return new ApiCredentials(getApiToken(context, judo), getApiSecret(context, judo));
    }

    private String getEncodedCredentials() {
        if (isEmpty(apiToken) || isEmpty(apiSecret)) {
            throw new TokenSecretError("API Token & Secret is not configured correctly, either:" +
                    "\t - Call to Judo.setup(\"token\", \"secret\", Judo.SANDBOX) in your Activity class" +
                    "\t - Add a meta-data attributes \"judo_api_token\" and \"judo_api_secret\" to your AndroidManifest.xml file");
        }
        return Base64.encodeToString(String.format("%s:%s", apiToken, apiSecret).getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    private String getAuthorization() {
        return authorization;
    }

    public String getBasicAuthorizationHeader() {
        return String.format("Basic %s", getAuthorization());
    }

    private static String getApiSecret(final Context context, final Judo judo) {
        return getManifestMetaData(context, API_SECRET_MANIFEST_NAME, judo.getApiSecret());
    }

    private static String getApiToken(final Context context, final Judo judo) {
        return getManifestMetaData(context, API_TOKEN_MANIFEST_NAME, judo.getApiToken());
    }

    private static String getManifestMetaData(final Context context, final String attribute, final String defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();

            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;

            String metaData = bundle.getString(attribute);
            if (metaData != null && metaData.length() > 0) {
                return metaData;
            }
        } catch (NullPointerException | PackageManager.NameNotFoundException ignore) {
        }

        return defaultValue;
    }

}
