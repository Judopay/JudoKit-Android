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

import java.util.List;

import rx.Single;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static com.judopay.TestUtil.JUDO_ID;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
                .setConsumerReference("PreAuthVoidTest")
                .build();

        TestSubscriber<Receipt> subscriber = new TestSubscriber<>();
        apiService.preAuth(paymentRequest)
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        VoidRequest voidTransaction = new VoidRequest(
                                receipt.getReceiptId(),
                                receipt.getAmount());

                        return apiService.voidPreAuth(voidTransaction);
                    }
                })
                .subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipt> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.get(0).isSuccess(), is(true));
    }

}