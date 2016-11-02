package com.judopay.samples;

import com.judopay.JudoApiClient;

public class Application extends android.app.Application {

    private JudoApiClient judoApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        judoApiClient = new JudoApiClient(this);

        // connect with device service
        judoApiClient.connect();
    }

    public String getDeviceId() {
        return judoApiClient.getDeviceId();
    }

}