package com.judopay.receipts;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.judopay.JudoApiService;
import com.judopay.model.CollectionRequest;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.Receipts;
import com.judopay.model.RefundRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.TestObserver;

import static com.judopay.TestUtil.JUDO_ID_IRIDIUM;
import static com.judopay.TestUtil.getJudo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ListReceiptsTest {
    private static final String CONSUMER_REF = "ListReceiptsTest";

    @Test
    public void listPaymentReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.payment(getPaymentRequest())
                .flatMap(receipt -> apiService.paymentReceipts(null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listPreAuthReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(receipt -> apiService.preAuthReceipts(null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listRefundReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.payment(getPaymentRequest())
                .flatMap(receipt -> apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount().toString())))
                .flatMap(receipt -> apiService.refundReceipts(null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listCollectionReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(receipt -> apiService.collection(new CollectionRequest(receipt.getReceiptId(), receipt.getAmount().toString())))
                .flatMap(receipt -> apiService.collectionReceipts(null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listConsumerReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.payment(getPaymentRequest())
                .flatMap(receipt -> apiService.consumerReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listReceiptsByReceiptId() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.payment(getPaymentRequest())
                .flatMap(receipt -> apiService.findReceipt(receipt.getReceiptId(), null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().get(0).isSuccess(), is(true));
    }

    @Test
    public void listConsumerPaymentReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.payment(getPaymentRequest())
                .flatMap(receipt -> apiService.consumerPaymentReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listConsumerPreAuthReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(receipt -> apiService.consumerPreAuthReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listConsumerCollectionReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.preAuth(getPaymentRequest())
                .flatMap(receipt -> apiService.collection(new CollectionRequest(receipt.getReceiptId(), receipt.getAmount().toString())))
                .flatMap(receipt -> apiService.consumerCollectionReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    @Test
    public void listConsumerRefundReceipts() {
        final JudoApiService apiService = getApiService();
        TestObserver<Receipts> testObserver = new TestObserver<>();

        apiService.payment(getPaymentRequest())
                .flatMap(receipt -> apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount().toString())))
                .flatMap(receipt -> apiService.consumerRefundReceipts(receipt.getConsumer().getConsumerToken(), null, null, "time-descending"))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().isEmpty(), is(false));
    }

    private JudoApiService getApiService() {
        return getJudo().getApiService(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    private PaymentRequest getPaymentRequest() {
        return new PaymentRequest.Builder()
                .setJudoId(JUDO_ID_IRIDIUM)
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setConsumerReference(CONSUMER_REF)
                .build();
    }
}
