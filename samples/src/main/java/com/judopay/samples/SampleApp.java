package com.judopay.samples;

import android.app.Application;

import com.judopay.JudoPay;

import static com.judopay.JudoPay.Environment.SANDBOX;

public class SampleApp extends Application {

    public static final String SHARED_PREFS_NAME = "Judo-SampleApp";
    public static final String CURRENCY_KEY = "Judo-SampleApp-Currency";

    @Override
    public void onCreate() {
        super.onCreate();

        JudoPay.setup(this, "823Eja2fEM6E9NAE","382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6", SANDBOX);
    }

}
