package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import okhttp3.Headers;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PreAuthPresenterTest {

    @Mock
    Receipt receipt;

    @Mock
    CardToken cardToken;

    @Mock
    JudoApiService apiService;

    @Mock
    TransactionCallbacks transactionCallbacks;

    private Gson gson = new Gson();
    private Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformPreAuth() {
        PreAuthPresenter presenter = new PreAuthPresenter(transactionCallbacks, apiService, scheduler, gson);
        when(apiService.preAuth(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performPreAuth(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("100915867")
                .build());

        verify(apiService).preAuth(any(PaymentRequest.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        PreAuthPresenter presenter = new PreAuthPresenter(transactionCallbacks, apiService, scheduler, gson);

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.preAuth(any(PaymentRequest.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performPreAuth(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerRef("consumerRef")
                .setJudoId("100915867")
                .build());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldPerformTokenPreAuth() {
        PreAuthPresenter presenter = new PreAuthPresenter(transactionCallbacks, apiService, new TestScheduler(), new Gson());
        when(apiService.tokenPreAuth(any(TokenRequest.class))).thenReturn(Observable.<Receipt>empty());
        when(cardToken.getToken()).thenReturn("cardToken");

        String consumer = "consumerRef";
        presenter.performTokenPreAuth(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setCardToken(cardToken)
                .setConsumerRef(consumer)
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setJudoId("100915867")
                .build());

        verify(transactionCallbacks).showLoading();
        verify(apiService).tokenPreAuth(any(TokenRequest.class));
    }

    public Card getCard() {
        return new Card.Builder()
                .setCardNumber("4976000000003436")
                .setSecurityCode("456")
                .setExpiryDate("12/21")
                .build();
    }

}