package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.UnknownHostException;

import okhttp3.Headers;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentPresenterTest {

    @Mock
    Card card;

    @Mock
    Receipt receipt;

    @Mock
    JudoApiService apiService;

    @Mock
    TransactionCallbacks transactionCallbacks;

    private Gson gson = new Gson();
    private Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformPayment() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);
        when(apiService.payment(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performPayment(card, new JudoOptions.Builder()
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("123456")
                .build());

        verify(apiService).payment(any(PaymentRequest.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);
        when(apiService.payment(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>error(new UnknownHostException()));

        presenter.performPayment(card, new JudoOptions.Builder()
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("123456")
                .build());

        verify(apiService).payment(any(PaymentRequest.class));
        verify(transactionCallbacks).onConnectionError();
    }

    @Test
    public void shouldReturnReceiptWhenHttpException() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);

        Buffer buffer = new Buffer();

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), buffer);

        HttpException exception = new HttpException(retrofit2.Response.error(404, responseBody));

        when(apiService.payment(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performPayment(card, new JudoOptions.Builder()
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("123456")
                .build());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.payment(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performPayment(card, new JudoOptions.Builder()
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("123456")
                .build());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

}