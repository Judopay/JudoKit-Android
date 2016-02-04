package com.judopay;

import android.app.Activity;
import android.content.Context;

import com.judopay.api.JudoApiServiceFactory;

/**
 * Lets you configure options for how the SDK interacts with the REST API, including setting of
 * your token and secret, enabling sandbox and live environments, changing supported card types,
 * allowing for payments routed through 3D-Secure and requiring Address Verification Checks (AVS).
 */
public class Judo {

    public static final int RESULT_SUCCESS = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int RESULT_DECLINED = 2;
    public static final int RESULT_ERROR = 1;

    public static final String JUDO_OPTIONS = "JudoPay-options";
    public static final String JUDO_RECEIPT = "JudoPay-receipt";

    public static final String JUDO_AMOUNT = "JudoPay-amount";
    public static final String JUDO_ID = "JudoPay-judoId";
    public static final String JUDO_CURRENCY = "JudoPay-currency";
    public static final String JUDO_META_DATA = "JudoPay-yourPaymentMetaData";
    public static final String JUDO_CARD_TOKEN = "JudoPay-cardToken";
    public static final String JUDO_CONSUMER = "JudoPay-consumer";
    public static final String JUDO_ALLOW_DECLINED_CARD_AMEND = "Judo-AllowDeclinedPaymentAmend";

    private static final String API_HOST_SANDBOX = "https://gw1.judopay-sandbox.com";
    private static final String API_HOST_LIVE = "https://gw1.judopay.com";

    private static String apiToken;
    private static String apiSecret;
    private static int apiEnvironment;

    private static boolean avsEnabled;
    private static boolean amexEnabled;

    private static boolean maestroEnabled = true;
    private static boolean sslPinningEnabled = true;
    private static boolean rootedDevicesAllowed = true;
    
    public static void setup(String apiToken, String apiSecret, int apiEnvironment) {
        Judo.apiToken = apiToken;
        Judo.apiSecret = apiSecret;
        Judo.apiEnvironment = apiEnvironment;
    }

    @SuppressWarnings("unused")
    public static JudoApiService getApiService(Context context) {
        return JudoApiServiceFactory.getInstance(context);
    }

    public static String getApiToken() {
        checkInitialised();
        return Judo.apiToken;
    }

    public static void setApiToken(String apiToken) {
        checkInitialised();
        Judo.apiToken = apiToken;
    }

    public static String getApiSecret() {
        checkInitialised();
        return apiSecret;
    }

    public static void setApiSecret(String apiSecret) {
        checkInitialised();
        Judo.apiSecret = apiSecret;
    }

    public static int getApiEnvironment() {
        checkInitialised();
        return apiEnvironment;
    }

    public static void setApiEnvironment(int apiEnvironment) {
        checkInitialised();
        Judo.apiEnvironment = apiEnvironment;
    }

    public static boolean isSslPinningEnabled() {
        checkInitialised();
        return sslPinningEnabled;
    }

    public static void setSslPinningEnabled(boolean sslPinningEnabled) {
        checkInitialised();
        Judo.sslPinningEnabled = sslPinningEnabled;
    }

    public static boolean isAvsEnabled() {
        checkInitialised();
        return avsEnabled;
    }

    public static void setAvsEnabled(boolean avsEnabled) {
        checkInitialised();
        Judo.avsEnabled = avsEnabled;
    }

    public static void setMaestroEnabled(boolean enabled) {
        checkInitialised();
        Judo.maestroEnabled = enabled;
    }

    public static boolean isMaestroEnabled() {
        checkInitialised();
        return maestroEnabled;
    }

    public static void setAmexEnabled(boolean enabled) {
        checkInitialised();
        Judo.amexEnabled = enabled;
    }

    public static boolean isAmexEnabled() {
        checkInitialised();
        return Judo.amexEnabled;
    }

    public static boolean isRootedDevicesAllowed() {
        checkInitialised();
        return rootedDevicesAllowed;
    }

    public static void setRootedDevicesAllowed(boolean rootedDevicesAllowed) {
        checkInitialised();
        Judo.rootedDevicesAllowed = rootedDevicesAllowed;
    }

    public static String getApiEnvironmentHost() {
        checkInitialised();
        return apiEnvironment == Environment.SANDBOX ? API_HOST_SANDBOX : API_HOST_LIVE;
    }

    private static void checkInitialised() {
        if (apiToken == null || apiSecret == null) {
            throw new RuntimeException("JudoPay SDK not initialised, call JudoPay.setup() with your API token and secret to configure");
        }
    }

    public class Environment {
        public static final int LIVE = 0;
        public static final int SANDBOX = 1;
    }

}