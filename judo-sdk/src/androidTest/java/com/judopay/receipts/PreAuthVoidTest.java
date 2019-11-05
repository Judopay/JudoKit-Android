package com.judopay.receipts;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.judopay.JudoApiService;
import com.judopay.api.Response;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.VoidRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;

import static com.judopay.TestUtil.JUDO_ID_IRIDIUM;
import static com.judopay.TestUtil.getJudo;

@RunWith(AndroidJUnit4.class)
public class PreAuthVoidTest {

    @Test
    public void shouldPreAuthAndVoidTransaction() {
        final JudoApiService apiService = getJudo().getApiService(InstrumentationRegistry.getInstrumentation().getTargetContext());

        PaymentRequest paymentRequest = new PaymentRequest.Builder()
                .setJudoId(JUDO_ID_IRIDIUM)
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setConsumerReference("PreAuthVoidTest")
                .build();

        TestObserver<Receipt> testObserver = new TestObserver<>();
        apiService.preAuth(paymentRequest)
                .flatMap((Function<Receipt, Single<Receipt>>) receipt -> {
                    VoidRequest voidTransaction = new VoidRequest(
                            receipt.getReceiptId(),
                            receipt.getAmount());

                    return apiService.voidPreAuth(voidTransaction);
                })
                .subscribe(testObserver);

        testObserver.assertNoErrors();
        testObserver.assertValue(Response::isSuccess);
    }
}
