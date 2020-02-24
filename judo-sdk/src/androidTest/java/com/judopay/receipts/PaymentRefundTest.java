package com.judopay.receipts;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.judopay.api.JudoApiService;
import com.judopay.model.Currency;
import com.judopay.api.model.request.PaymentRequest;
import com.judopay.api.model.response.Receipt;
import com.judopay.api.model.request.RefundRequest;

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
        final JudoApiService apiService = getJudo().getApiService(InstrumentationRegistry.getInstrumentation().getTargetContext());

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
