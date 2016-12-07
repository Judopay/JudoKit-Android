package com.judopay.androidpay;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.JudoApiService;
import com.judopay.model.AndroidPayRequest;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.model.Wallet;
import com.judopay.receipts.RxHelpers;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static com.judopay.TestSubscribers.assertResponseSuccessful;
import static com.judopay.TestSubscribers.fail;
import static com.judopay.TestUtil.getJudo;

@RunWith(AndroidJUnit4.class)
public class AndroidPayTest {

    private static final String ENCRYPTED_MESSAGE = "ZW5jcnlwdGVkTWVzc2FnZQ==";
    private static final String EPHEMERAL_PUBLIC_KEY = "ZXBoZW1lcmFsUHVibGljS2V5";
    private static final String PUBLIC_KEY = "BM7LpaTrg9lzjWJVmiEGMUhLmnUaNh+hQeCavCaOmPqVU6KEj7VSquZzkhCkQOWMiFHiUt/wzdw/pFccmjHZXk4=";
    private static final String TAG = "c2lnbmF0dXJl";
    private static final String JUDO_ID = "1234567";

    /**
     * The Android Pay wallet environment, as defined in com.google.android.gms.wallet.WalletConstants
     */
    private static final int ENVIRONMENT_TEST = 3;

    @Test
    public void shouldReturnSuccessWhenAndroidPayPayment() {
        Context context = InstrumentationRegistry.getContext();
        JudoApiService apiService = getJudo().getApiService(context);

        AndroidPayRequest androidPayRequest = getAndroidPayRequest();

        apiService.androidPayPayment(androidPayRequest)
                .compose(RxHelpers.<Receipt>schedulers())
                .subscribe(assertResponseSuccessful(), fail());
    }

    @Test
    public void shouldReturnSuccessWhenAndroidPayPreAuth() {
        Context context = InstrumentationRegistry.getContext();
        JudoApiService apiService = getJudo().getApiService(context);

        AndroidPayRequest androidPayRequest = getAndroidPayRequest();
        apiService.androidPayPreAuth(androidPayRequest)
                .compose(RxHelpers.<Receipt>schedulers())
                .subscribe(assertResponseSuccessful(), fail());
    }

    private AndroidPayRequest getAndroidPayRequest() {
        return new AndroidPayRequest.Builder()
                .setJudoId(JUDO_ID)
                .setWallet(new Wallet.Builder()
                        .setEncryptedMessage(ENCRYPTED_MESSAGE)
                        .setEphemeralPublicKey(EPHEMERAL_PUBLIC_KEY)
                        .setEnvironment(ENVIRONMENT_TEST)
                        .setTag(TAG)
                        .setPublicKey(PUBLIC_KEY)
                        .setInstrumentDetails("1234")
                        .setInstrumentType("VISA")
                        .setGoogleTransactionId("123456789")
                        .setVersion(1)
                        .build())
                .setAmount(new BigDecimal("0.10"))
                .setConsumerReference("AndroidPayTest")
                .setCurrency(Currency.GBP)
                .build();
    }

}