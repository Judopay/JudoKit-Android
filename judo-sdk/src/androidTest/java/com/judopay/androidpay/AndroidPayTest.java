package com.judopay.androidpay;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.JudoApiService;
import com.judopay.model.AndroidPayRequest;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.model.Wallet;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import rx.observers.TestSubscriber;

import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
    @Ignore
    public void shouldReturnSuccessWhenAndroidPayPayment() {
        Context context = InstrumentationRegistry.getContext();
        
        JudoApiService apiService = getJudo().getApiService(context);
        TestSubscriber<Receipt> subscriber = new TestSubscriber<>();

        apiService.androidPayPayment(getAndroidPayRequest())
                .subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipt> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.get(0).isSuccess(), is(true));
    }

    @Test
    @Ignore
    public void shouldReturnSuccessWhenAndroidPayPreAuth() {
        Context context = InstrumentationRegistry.getContext();

        JudoApiService apiService = getJudo().getApiService(context);
        TestSubscriber<Receipt> subscriber = new TestSubscriber<>();

        AndroidPayRequest androidPayRequest = getAndroidPayRequest();
        apiService.androidPayPreAuth(androidPayRequest)
                .subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipt> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.get(0).isSuccess(), is(true));
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
                .setAmount("0.10")
                .setConsumerReference("AndroidPayTest")
                .setCurrency(Currency.GBP)
                .build();
    }
}
