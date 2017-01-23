package com.judopay.receipts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.JudoApiService;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.VoidRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Single;
import rx.functions.Func1;

import static com.judopay.TestUtil.JUDO_ID;
import static com.judopay.TestUtil.getJudo;

@RunWith(AndroidJUnit4.class)
public class PreAuthVoidTest {

    @Test
    public void shouldPreAuthAndVoidTransaction() {
        Context context = InstrumentationRegistry.getContext();

        final JudoApiService apiService = getJudo().getApiService(context);

        PaymentRequest paymentRequest = new PaymentRequest.Builder()
                .setJudoId(JUDO_ID)
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setYourConsumerReference("PreAuthVoidTest")
                .build();

        apiService.preAuth(paymentRequest)
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        VoidRequest voidTransaction = new VoidRequest(
                                receipt.getReceiptId(),
                                receipt.getAmount());

                        return apiService.voidPreAuth(voidTransaction);
                    }
                })
                .subscribe(RxHelpers.assertTransactionSuccessful(), RxHelpers.failOnError());
    }

}