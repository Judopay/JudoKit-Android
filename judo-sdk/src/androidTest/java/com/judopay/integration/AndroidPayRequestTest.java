package com.judopay.integration;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.model.AndroidPayRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.schedulers.Schedulers;

import static com.judopay.integration.TestSubscribers.assertResponseSuccessful;
import static com.judopay.integration.TestSubscribers.fail;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class AndroidPayRequestTest {

    @Before
    public void setupJudoSdk() {
        Judo.setup("823Eja2fEM6E9NAE", "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6", Judo.Environment.SANDBOX);
    }

    @Test
    public void shouldReturnSuccessWhenAndroidPayPayment() {
        Context context = InstrumentationRegistry.getContext();
        final JudoApiService apiService = Judo.getApiService(context);

        AndroidPayRequest androidPayRequest = new AndroidPayRequest.Builder()
                .setEncryptedMessage("ZW5jcnlwdGVkTWVzc2FnZQ==")
                .setEphemeralPublicKey("ZXBoZW1lcmFsUHVibGljS2V5")
                .setTag("c2lnbmF0dXJl")
                .build();

        apiService.androidPayPayment(androidPayRequest)
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(assertResponseSuccessful(), fail());
    }

    @Test
    public void shouldReturnSuccessWhenAndroidPayPreAuth() {
        Context context = InstrumentationRegistry.getContext();
        final JudoApiService apiService = Judo.getApiService(context);

        AndroidPayRequest androidPayRequest = new AndroidPayRequest.Builder()
                .setEncryptedMessage("ZW5jcnlwdGVkTWVzc2FnZQ==")
                .setEphemeralPublicKey("ZXBoZW1lcmFsUHVibGljS2V5")
                .setTag("c2lnbmF0dXJl")
                .build();

        apiService.androidPayPreAuth(androidPayRequest)
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .subscribe(assertResponseSuccessful(), fail());
    }

}
