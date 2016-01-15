package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.PaymentTransaction;
import com.judopay.model.Receipt;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.internal.http.RealResponseBody;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.UnknownHostException;

import okio.Buffer;
import retrofit.HttpException;
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
    Address cardAddress;

    @Mock
    JudoApiService apiService;

    @Mock
    PaymentFormView paymentFormView;

    Gson gson = new Gson();
    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformPayment() {
        PaymentPresenter presenter = new PaymentPresenter(paymentFormView, apiService, scheduler, gson);
        when(apiService.payment(any(PaymentTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performPayment(card, "consumerRef", "123456", "1.99", "GBP", null);

        verify(apiService).payment(any(PaymentTransaction.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        PaymentPresenter presenter = new PaymentPresenter(paymentFormView, apiService, scheduler, gson);
        when(apiService.payment(any(PaymentTransaction.class))).thenReturn(Observable.<Receipt>error(new UnknownHostException()));

        presenter.performPayment(card, "consumerRef", "123456", "1.99", "GBP", null);
        verify(apiService).payment(any(PaymentTransaction.class));

        verify(paymentFormView).showConnectionErrorDialog();
    }

    @Test
    public void shouldReturnReceiptWhenHttpException() {
        PaymentPresenter presenter = new PaymentPresenter(paymentFormView, apiService, scheduler, gson);

        Buffer buffer = new Buffer();

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), buffer);

        HttpException exception = new HttpException(retrofit.Response.error(404, responseBody));

        when(apiService.payment(any(PaymentTransaction.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performPayment(card, "consumerRef", "123456", "1.99", "GBP", null);
        verify(paymentFormView).showDeclinedMessage(any(Receipt.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        PreAuthPresenter presenter = new PreAuthPresenter(paymentFormView, apiService, scheduler, gson);

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit.Response.error(400, responseBody));

        when(apiService.preAuth(any(PaymentTransaction.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performPreAuth(card, "consumerRef", "123456", "1.99", "GBP", null);
        verify(paymentFormView).showDeclinedMessage(any(Receipt.class));
    }

}