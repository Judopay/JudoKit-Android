package com.judopay.samples;

import android.app.Application;

import com.judopay.JudoPay;

import static com.judopay.JudoPay.Environment.LIVE;

public class SampleApp extends Application {

    public static final String SHARED_PREFS_NAME = "Judo-SampleApp";
    public static final String CURRENCY_KEY = "Judo-SampleApp-Currency";

    @Override
    public void onCreate() {
        super.onCreate();

        JudoPay.setup(this, "M2VcTpiyM05eZwOr","71479addc43b5424b95f22407ca96f8503709ddede67c596af393d5b93ff4bd7", LIVE);
    }

}
