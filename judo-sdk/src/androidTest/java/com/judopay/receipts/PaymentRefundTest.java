package com.judopay.receipts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.JudoApiService;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.RefundRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import rx.Single;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static com.judopay.TestUtil.JUDO_ID;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class PaymentRefundTest {

    @Test
    public void shouldRefundPayment() {
        Context context = InstrumentationRegistry.getContext();
        final JudoApiService apiService = getJudo().getApiService(context);

        PaymentRequest paymentRequest = new PaymentRequest.Builder()
                .setJudoId(JUDO_ID)
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setConsumerReference("PreAuthCollectionTest")
                .build();

        TestSubscriber<Receipt> testSubscriber = new TestSubscriber<>();

        apiService.payment(paymentRequest)
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        return apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount().toString()));
                    }
                })
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Receipt> receipts = testSubscriber.getOnNextEvents();

        assertThat(receipts.get(0).isSuccess(), is(true));
    }
}