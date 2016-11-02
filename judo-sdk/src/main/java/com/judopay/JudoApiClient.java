package com.judopay;

import android.content.Context;

import com.judopay.arch.AndroidScheduler;
import com.judopay.shield.JudoShield;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class JudoApiClient {

    private String deviceId;

    private Context context;

    public JudoApiClient(Context context) {
        this.context = context.getApplicationContext();
        this.deviceId = "unknown";
    }

    public void connect() {
        JudoShield judoShield = new JudoShield(context);
        judoShield.getDeviceId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        JudoApiClient.this.deviceId = result;
                    }
                });
    }

    public String getDeviceId() {
        return deviceId;
    }
}
