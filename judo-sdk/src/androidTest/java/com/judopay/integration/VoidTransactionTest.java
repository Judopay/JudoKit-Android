package com.judopay.integration;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.VoidRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class VoidTransactionTest {

    @Before
    public void setupJudoSdk() {
        Judo.setup("823Eja2fEM6E9NAE", "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6", Judo.Environment.SANDBOX);
    }

    @Test
    public void shouldPreAuthAndVoidTransaction() {
        Context context = InstrumentationRegistry.getContext();
        final JudoApiService apiService = Judo.getApiService(context);

        PaymentRequest paymentRequest = new PaymentRequest.Builder()
                .setJudoId("100407196")
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setYourConsumerReference("VoidTransactionTest")
                .build();

        apiService.preAuth(paymentRequest)
                .observeOn(Schedulers.immediate())
                .subscribeOn(Schedulers.immediate())
                .flatMap(new Func1<Receipt, Observable<Receipt>>() {
                    @Override
                    public Observable<Receipt> call(Receipt receipt) {
                        VoidRequest voidTransaction = new VoidRequest(
                                receipt.getReceiptId(),
                                receipt.getAmount());

                        return apiService.voidPreAuth(voidTransaction);
                    }
                })
                .subscribe(new Action1<Receipt>() {
                    @Override
                    public void call(Receipt receipt) {
                        assertThat(receipt.isSuccess(), is(true));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        fail();
                    }
                });
    }

}