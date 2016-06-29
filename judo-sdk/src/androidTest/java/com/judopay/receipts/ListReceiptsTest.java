package com.judopay.receipts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import com.judopay.Judo;
import com.judopay.JudoApiService;
import com.judopay.model.CollectionRequest;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.Receipts;
import com.judopay.model.RefundRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.functions.Func1;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ListReceiptsTest {

    private static final String CONSUMER_REF = "ListReceiptsTest";

    @Test
    public void listPaymentReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.payment(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.paymentReceipts(null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listPreAuthReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.preAuth(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.preAuthReceipts(null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listRefundReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.payment(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipt>>() {
                    @Override
                    public Observable<Receipt> call(Receipt receipt) {
                        return apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount()));
                    }
                })
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.refundReceipts(null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listCollectionReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.preAuth(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipt>>() {
                    @Override
                    public Observable<Receipt> call(Receipt receipt) {
                        return apiService.collection(new CollectionRequest(receipt.getReceiptId(), receipt.getAmount()));
                    }
                })
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.collectionReceipts(null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listConsumerReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.payment(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.consumerReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listReceiptsByReceiptId() {
        final JudoApiService apiService = getApiService();

        apiService.payment(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipt>>() {
                    @Override
                    public Observable<Receipt> call(Receipt receipt) {
                        return apiService.findReceipt(receipt.getReceiptId(), null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertTransactionSuccessful(), RxHelpers.failOnError());
    }

    @Test
    public void listConsumerPaymentReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.payment(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.consumerPaymentReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listConsumerPreAuthReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.preAuth(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.consumerPreAuthReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listConsumerCollectionReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.preAuth(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipt>>() {
                    @Override
                    public Observable<Receipt> call(Receipt receipt) {
                        return apiService.collection(new CollectionRequest(receipt.getReceiptId(), receipt.getAmount()));
                    }
                })
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.consumerCollectionReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    @Test
    public void listConsumerRefundReceipts() {
        final JudoApiService apiService = getApiService();

        apiService.payment(getPaymentRequest())
                .compose(RxHelpers.<Receipt>schedulers())
                .flatMap(new Func1<Receipt, Observable<Receipt>>() {
                    @Override
                    public Observable<Receipt> call(Receipt receipt) {
                        return apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount()));
                    }
                })
                .flatMap(new Func1<Receipt, Observable<Receipts>>() {
                    @Override
                    public Observable<Receipts> call(Receipt receipt) {
                        return apiService.consumerRefundReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending");
                    }
                })
                .subscribe(RxHelpers.assertHasReceipts(), RxHelpers.failOnError());
    }

    private JudoApiService getApiService() {
        Context context = InstrumentationRegistry.getContext();

        Judo judo = new Judo.Builder()
                .setJudoId("100915867")
                .setEnvironment(Judo.UAT)
                .build();

        return judo.getApiService(context);
    }

    private PaymentRequest getPaymentRequest() {
        return new PaymentRequest.Builder()
                .setJudoId("100915867")
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setYourConsumerReference(CONSUMER_REF)
                .build();
    }

}