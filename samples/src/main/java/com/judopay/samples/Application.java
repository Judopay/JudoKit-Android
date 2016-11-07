package com.judopay.samples;

import com.judopay.JudoDeviceClient;

public class Application extends android.app.Application {

    private JudoDeviceClient judoDeviceClient;

    @Override
    public void onCreate() {
        super.onCreate();

        judoDeviceClient = new JudoDeviceClient(this);

        // load with device service
        judoDeviceClient.identifyDevice();
    }

    public String getDeviceId() {
        return judoDeviceClient.cachedDeviceId();
    }

}