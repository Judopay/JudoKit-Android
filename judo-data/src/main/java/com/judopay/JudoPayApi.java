package com.judopay;

public class JudoPayApi {

    private int judoId;
    private String apiToken;
    private String apiSecret;
    private int apiEnvironment;

    private boolean sslPinningEnabled = true;
    private boolean avsEnabled;
    private boolean maestroEnabled;
    private boolean amexEnabled;

    public JudoPayApi(String apiToken, String apiSecret, int apiEnvironment) {
        this.apiToken = apiToken;
        this.apiSecret = apiSecret;
        this.apiEnvironment = apiEnvironment;
    }

    public int getJudoId() {
        return judoId;
    }

    public void setJudoId(int judoId) {
        this.judoId = judoId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public int getApiEnvironment() {
        return apiEnvironment;
    }

    public void setApiEnvironment(int apiEnvironment) {
        this.apiEnvironment = apiEnvironment;
    }

    public boolean isSslPinningEnabled() {
        return sslPinningEnabled;
    }

    public void setSslPinningEnabled(boolean sslPinningEnabled) {
        this.sslPinningEnabled = sslPinningEnabled;
    }

    public boolean isAvsEnabled() {
        return avsEnabled;
    }

    public void setAvsEnabled(boolean avsEnabled) {
        this.avsEnabled = avsEnabled;
    }

    public void setMaestroEnabled(boolean enabled) {
        this.maestroEnabled = enabled;
    }

    public boolean isMaestroEnabled() {
        return maestroEnabled;
    }

    public void setAmexEnabled(boolean enabled) {
        this.amexEnabled = enabled;
    }

    public boolean isAmexEnabled() {
        return this.amexEnabled;
    }
}