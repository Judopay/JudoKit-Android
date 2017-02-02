package com.judopay.receipts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.judopay.JudoApiService;
import com.judopay.model.CollectionRequest;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.Receipts;
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
public class ListReceiptsTest {

    private static final String CONSUMER_REF = "ListReceiptsTest";

    @Test
    public void listPaymentReceipts() {
        final JudoApiService apiService = getApiService();

        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.payment(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.paymentReceipts(null, null, "time-descending");
                    }
                })
                .subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listPreAuthReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.preAuthReceipts(null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listRefundReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.payment(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        return apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount().toString()));
                    }
                })
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.refundReceipts(null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listCollectionReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        return apiService.collection(new CollectionRequest(receipt.getReceiptId(), receipt.getAmount().toString()));
                    }
                })
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.collectionReceipts(null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listConsumerReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.payment(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.consumerReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listReceiptsByReceiptId() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipt> subscriber = new TestSubscriber<>();

        apiService.payment(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        return apiService.findReceipt(receipt.getReceiptId(), null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipt> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.get(0).isSuccess(), is(true));
    }

    @Test
    public void listConsumerPaymentReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.payment(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.consumerPaymentReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listConsumerPreAuthReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.consumerPreAuthReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listConsumerCollectionReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        return apiService.collection(new CollectionRequest(receipt.getReceiptId(), receipt.getAmount().toString()));
                    }
                })
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.consumerCollectionReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    @Test
    public void listConsumerRefundReceipts() {
        final JudoApiService apiService = getApiService();
        TestSubscriber<Receipts> subscriber = new TestSubscriber<>();

        apiService.payment(getPaymentRequest())
                .flatMap(new Func1<Receipt, Single<Receipt>>() {
                    @Override
                    public Single<Receipt> call(Receipt receipt) {
                        return apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount().toString()));
                    }
                })
                .flatMap(new Func1<Receipt, Single<Receipts>>() {
                    @Override
                    public Single<Receipts> call(Receipt receipt) {
                        return apiService.consumerRefundReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                }).subscribe(subscriber);

        subscriber.assertNoErrors();
        List<Receipts> receipts = subscriber.getOnNextEvents();

        assertThat(receipts.isEmpty(), is(false));
    }

    private JudoApiService getApiService() {
        Context context = InstrumentationRegistry.getContext();

        return getJudo().getApiService(context);
    }

    private PaymentRequest getPaymentRequest() {
        return new PaymentRequest.Builder()
                .setJudoId(JUDO_ID)
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setConsumerReference(CONSUMER_REF)
                .build();
    }

}