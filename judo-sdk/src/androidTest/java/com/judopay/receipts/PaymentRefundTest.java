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

import io.reactivex.observers.TestObserver;

import static com.judopay.TestUtil.JUDO_ID_IRIDIUM;
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
                .setJudoId(JUDO_ID_IRIDIUM)
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setConsumerReference("PreAuthCollectionTest")
                .build();

        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.payment(paymentRequest)
                .flatMap(receipt -> apiService.refund(new RefundRequest(receipt.getReceiptId(), receipt.getAmount().toString())))
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        assertThat(testObserver.values().get(0).isSuccess(), is(true));
    }
}
