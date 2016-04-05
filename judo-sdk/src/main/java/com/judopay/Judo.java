package com.judopay;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;

import com.judopay.api.JudoApiServiceFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Lets you configure options for how the SDK interacts with the REST API, including setting of
 * your token and secret, enabling sandbox and live environments, changing supported card types,
 * allowing for payments routed through 3D-Secure and requiring Address Verification Checks (AVS).
 */
public class Judo {

    @IntDef({UI_CLIENT_MODE_CUSTOM_UI, UI_CLIENT_MODE_JUDO_SDK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UiClientMode {
    }

    @IntDef({LIVE, SANDBOX, UAT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Environment {
    }

    public static final int RESULT_SUCCESS = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int RESULT_DECLINED = 2;
    public static final int RESULT_ERROR = 1;
    public static final int RESULT_TOKEN_EXPIRED = 3;
    public static final int RESULT_CONNECTION_ERROR = 5;

    public static final int JUDO_REQUEST = 100;

    // Constants to define different actions (for use with startActivityForResult(...))
    public static final int PAYMENT_REQUEST = 101;
    public static final int TOKEN_PAYMENT_REQUEST = 102;
    public static final int PRE_AUTH_REQUEST = 201;
    public static final int TOKEN_PRE_AUTH_REQUEST = 202;
    public static final int REGISTER_CARD_REQUEST = 301;

    public static final String JUDO_OPTIONS = "JudoPay-options";
    public static final String JUDO_RECEIPT = "JudoPay-receipt";

    public static final int UI_CLIENT_MODE_CUSTOM_UI = 0;
    public static final int UI_CLIENT_MODE_JUDO_SDK = 1;

    public static final int LIVE = 0;
    public static final int SANDBOX = 1;
    public static final int UAT = 3;

    private static String apiToken;
    private static String apiSecret;

    private static int environment;

    private static boolean avsEnabled;
    private static boolean amexEnabled;

    private static boolean maestroEnabled = true;
    private static boolean sslPinningEnabled = true;
    private static boolean rootedDevicesAllowed = true;

    public static void setup(String apiToken, String apiSecret, @Environment int environment) {
        Judo.apiToken = apiToken;
        Judo.apiSecret = apiSecret;
        Judo.environment = environment;
    }

    @Environment
    public static int getEnvironment() {
        return environment;
    }

    public static JudoApiService getApiService(Context context) {
        return JudoApiServiceFactory.createApiService(context, UI_CLIENT_MODE_CUSTOM_UI);
    }

    static JudoApiService getApiService(Context context, @UiClientMode int uiClientMode) {
        return JudoApiServiceFactory.createApiService(context, uiClientMode);
    }

    public static void setEnvironment(@Environment int environment) {
        Judo.environment = environment;
    }

    public static String getApiToken() {
        return Judo.apiToken;
    }

    public static String getApiSecret() {
        return apiSecret;
    }

    public static boolean isSslPinningEnabled() {
        return sslPinningEnabled && environment != UAT;
    }

    public static void setSslPinningEnabled(boolean sslPinningEnabled) {
        Judo.sslPinningEnabled = sslPinningEnabled;
    }

    public static boolean isAvsEnabled() {
        return avsEnabled;
    }

    public static void setAvsEnabled(boolean avsEnabled) {
        Judo.avsEnabled = avsEnabled;
    }

    public static void setMaestroEnabled(boolean enabled) {
        Judo.maestroEnabled = enabled;
    }

    public static boolean isMaestroEnabled() {
        return maestroEnabled;
    }

    public static void setAmexEnabled(boolean enabled) {
        Judo.amexEnabled = enabled;
    }

    public static boolean isAmexEnabled() {
        return Judo.amexEnabled;
    }

    public static boolean isRootedDevicesAllowed() {
        return rootedDevicesAllowed;
    }

    public static void setRootedDevicesAllowed(boolean rootedDevicesAllowed) {
        Judo.rootedDevicesAllowed = rootedDevicesAllowed;
    }

    public static String getApiEnvironmentHost(Context context) {
        switch (environment) {
            case SANDBOX:
                return context.getString(R.string.api_host_sandbox);
            case UAT:
                return context.getString(R.string.api_host_uat);
            default:
                return context.getString(R.string.api_host_live);
        }
    }

}