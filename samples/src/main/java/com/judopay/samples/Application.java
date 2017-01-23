package com.judopay.samples;

import com.judopay.DeviceDna;

public class Application extends android.app.Application {

    private DeviceDna deviceDna;

    @Override
    public void onCreate() {
        super.onCreate();

        deviceDna = new DeviceDna(this);

        // load with device service
        deviceDna.identifyDevice()
                .subscribe();
    }

    public String getDeviceId() {
        return deviceDna.cachedDeviceId();
    }
}