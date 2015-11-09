package com.judopay.samples;

import android.app.Application;

import com.judopay.JudoPay;

import static com.judopay.JudoPay.Environment.SANDBOX;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JudoPay.setup(this, "7hljj3yIMwmj8uAy","a8e09ea2299896e6ef4279cb19c1e37bbf04b94650f29ef95721319264ffa00a", SANDBOX);
    }

}
