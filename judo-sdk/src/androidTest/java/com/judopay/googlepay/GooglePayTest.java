package com.judopay.googlepay;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.JudoApiService;
import com.judopay.model.GooglePayRequest;
import com.judopay.model.Currency;
import com.judopay.model.GooglePayWallet;
import com.judopay.model.Receipt;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.TestObserver;

import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class GooglePayTest {

    @Test
    @Ignore
    public void shouldReturnSuccessWhenGooglePayPayment() {
        Context context = InstrumentationRegistry.getContext();

        JudoApiService apiService = getJudo().getApiService(context);
        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.googlePayPayment(getAndroidPayRequest()).subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().get(0).isSuccess(), is(true));
    }

    @Test
    @Ignore
    public void shouldReturnSuccessWhenGooglePayPreAuth() {
        Context context = InstrumentationRegistry.getContext();

        JudoApiService apiService = getJudo().getApiService(context);
        TestObserver<Receipt> testObserver = new TestObserver<>();

        GooglePayRequest googlePayRequest = getAndroidPayRequest();
        apiService.googlePayPreAuth(googlePayRequest).subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().get(0).isSuccess(), is(true));
    }

    private GooglePayRequest getAndroidPayRequest() {
        return new GooglePayRequest.Builder()
                .setJudoId("1234567")
                .setGooglePayWallet(new GooglePayWallet.Builder()
                        .setCardNetwork("VISA")
                        .setCardDetails("1234")
                        .setToken("{\"something\": \"something\"}")
                        .build())
                .setAmount("0.10")
                .setCurrency(Currency.GBP)
                .setConsumerReference("GooglePayTest")
                .build();
    }
}
