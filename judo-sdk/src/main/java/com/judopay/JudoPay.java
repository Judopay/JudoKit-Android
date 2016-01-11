package com.judopay;

import android.app.Activity;

/**
 * Lets you configure options for how the SDK interacts with the REST API, including setting of
 * your token and secret, enabling sandbox and live environments, changing supported card types,
 * allowing for payments routed through 3D-Secure and requiring Address Verification Checks (AVS).
 */
public class JudoPay {

    public static final int RESULT_SUCCESS = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int RESULT_DECLINED = 2;
    public static final int RESULT_ERROR = 1;

    public static final String JUDO_AMOUNT = "JudoPay-amount";
    public static final String JUDO_ID = "JudoPay-judoId";
    public static final String JUDO_CURRENCY = "JudoPay-currency";
    public static final String JUDO_META_DATA = "JudoPay-yourPaymentMetaData";
    public static final String JUDO_RECEIPT = "JudoPay-receipt";

    public static final String JUDO_CARD_TOKEN = "JudoPay-cardToken";
    public static final String JUDO_CONSUMER = "JudoPay-consumer";

    public static final String JUDO_ALLOW_DECLINED_CARD_AMEND = "Judo-AllowDeclinedPaymentAmend";

    private static final String API_HOST_SANDBOX = "https://gw1.judopay-sandbox.com";
    private static final String API_HOST_LIVE = "https://gw1.judopay.com";

    private static JudoPayApi api;

    public static void setup(String apiToken, String apiSecret, int apiEnvironment) {
        api = new JudoPayApi(apiToken, apiSecret, apiEnvironment);
    }

    public static String getApiEnvironmentHost() {
        checkInitialised();
        return api.getApiEnvironment() == Environment.SANDBOX ? API_HOST_SANDBOX : API_HOST_LIVE;
    }

    public static boolean isSslPinningEnabled() {
        checkInitialised();
        return api.isSslPinningEnabled();
    }

    public static void setSslPinningEnabled(boolean enabled) {
        checkInitialised();
        api.setSslPinningEnabled(enabled);
    }

    public static void setAvsEnabled(boolean enabled) {
        checkInitialised();
        api.setAvsEnabled(enabled);
    }

    public static boolean isAvsEnabled() {
        checkInitialised();
        return api.isAvsEnabled();
    }

    public static void setMaestroEnabled(boolean enabled) {
        checkInitialised();
        api.setMaestroEnabled(enabled);
    }

    public static boolean isMaestroEnabled() {
        checkInitialised();
        return api.isMaestroEnabled();
    }

    public static void setAmexEnabled(boolean enabled) {
        checkInitialised();
        api.setAmexEnabled(enabled);
    }

    public static boolean isAmexEnabled() {
        checkInitialised();
        return api.isAmexEnabled();
    }

    public static boolean isThreeDSecureEnabled() {
        checkInitialised();
        return api.isThreeDSecureEnabled();
    }

    public static void setThreeDSecureEnabled(boolean enabled) {
        checkInitialised();
        api.setThreeDSecureEnabled(enabled);
    }

    public static void setRootedDevicesAllowed(boolean allowed) {
        checkInitialised();
        api.setRootedDevicesAllowed(allowed);
    }

    public static boolean isRootedDevicesAllowed() {
        checkInitialised();
        return api.isRootedDevicesAllowed();
    }

    public static String getApiToken() {
        checkInitialised();
        return api.getApiToken();
    }

    public static String getApiSecret() {
        checkInitialised();
        return api.getApiSecret();
    }

    private static void checkInitialised() {
        if (api == null) {
            throw new RuntimeException("JudoPay SDK not initialised, call JudoPay.setup() with your API token and secret to configure");
        }
    }

    public class Environment {
        public static final int LIVE = 0;
        public static final int SANDBOX = 1;
    }

}