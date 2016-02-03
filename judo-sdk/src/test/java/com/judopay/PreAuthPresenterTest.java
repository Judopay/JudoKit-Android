package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.Currency;
import com.judopay.model.PaymentTransaction;
import com.judopay.model.Receipt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import okhttp3.Headers;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.HttpException;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PreAuthPresenterTest {

    @Mock
    Card card;

    @Mock
    Receipt receipt;

    @Mock
    JudoApiService apiService;

    @Mock
    PaymentFormView paymentFormView;

    private Gson gson = new Gson();
    private Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformPreAuth() {
        PreAuthPresenter presenter = new PreAuthPresenter(paymentFormView, apiService, scheduler, gson);
        when(apiService.preAuth(any(PaymentTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performPreAuth(card, new JudoOptions.Builder()
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("123456")
                .build());

        verify(apiService).preAuth(any(PaymentTransaction.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        PreAuthPresenter presenter = new PreAuthPresenter(paymentFormView, apiService, scheduler, gson);

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.preAuth(any(PaymentTransaction.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performPreAuth(card, new JudoOptions.Builder()
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("123456")
                .build());

        verify(paymentFormView).showDeclinedMessage(any(Receipt.class));
    }

}