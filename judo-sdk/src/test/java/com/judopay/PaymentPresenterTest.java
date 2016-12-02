package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.CardVerificationResult;
import com.judopay.model.TokenRequest;

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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentPresenterTest {

    @Mock
    CardToken cardToken;

    @Mock
    CardVerificationResult cardVerificationResult;

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

        presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("100407196")
                .build());

        verify(apiService).payment(any(PaymentRequest.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);
        when(apiService.payment(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>error(new UnknownHostException()));

        presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("100407196")
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

        presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("100407196")
                .build());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.payment(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("100407196")
                .build());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldPerformTokenPayment() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);
        when(apiService.tokenPayment(any(TokenRequest.class))).thenReturn(Observable.<Receipt>empty());

        when(cardToken.getToken()).thenReturn("cardToken");

        String consumer = "consumerRef";
        presenter.performTokenPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setCardToken(cardToken)
                .setConsumerRef(consumer)
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setJudoId("100407196")
                .build());

        verify(transactionCallbacks).showLoading();
        verify(apiService).tokenPayment(any(TokenRequest.class));
    }

    @Test
    public void shouldFinishWhenSuccessfulReceipt() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(true);
        when(apiService.complete3dSecure(receiptId, cardVerificationResult)).thenReturn(Observable.just(receipt));

        presenter.onAuthorizationCompleted(cardVerificationResult, receiptId);

        verify(transactionCallbacks).onSuccess(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclinedReceipt() {
        PaymentPresenter presenter = new PaymentPresenter(transactionCallbacks, apiService, scheduler, gson);
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(false);
        when(apiService.complete3dSecure(receiptId, cardVerificationResult)).thenReturn(Observable.just(receipt));

        presenter.onAuthorizationCompleted(cardVerificationResult, "123456");

        verify(transactionCallbacks).onDeclined(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    private Card getCard() {
        return new Card.Builder()
                .setCardNumber("4976000000003436")
                .setSecurityCode("456")
                .setExpiryDate("12/21")
                .build();
    }

}