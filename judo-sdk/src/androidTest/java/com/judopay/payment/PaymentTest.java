package com.judopay.payment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.judopay.api.JudoApiService;
import com.judopay.model.Currency;
import com.judopay.api.model.request.PaymentRequest;
import com.judopay.api.model.response.Receipt;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.TestObserver;

import static com.judopay.TestUtil.JUDO_ID_IRIDIUM;
import static com.judopay.TestUtil.getJudo;
import static com.judopay.api.factory.JudoApiServiceFactory.createApiService;
import static java.util.UUID.randomUUID;

@RunWith(AndroidJUnit4.class)
public class PaymentTest {

    @Test
    public void shouldPerformPayment() {
        final JudoApiService apiService = createApiService(InstrumentationRegistry.getInstrumentation().getTargetContext(), Judo.UI_CLIENT_MODE_JUDO_SDK, getJudo());

        final PaymentRequest paymentRequest = new PaymentRequest.Builder()
                .setJudoId(JUDO_ID_IRIDIUM)
                .setAmount("0.01")
                .setCardNumber("4976000000003436")
                .setCv2("452")
                .setExpiryDate("12/20")
                .setCurrency(Currency.GBP)
                .setConsumerReference(randomUUID().toString())
                .build();

        TestObserver<Receipt> testObserver = new TestObserver<>();

        apiService.payment(paymentRequest).subscribe(testObserver);

        testObserver.assertNoErrors();
    }
}
