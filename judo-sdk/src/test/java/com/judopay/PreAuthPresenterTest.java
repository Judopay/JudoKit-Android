package com.judopay;

import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import okhttp3.Headers;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.adapter.rxjava.HttpException;
import rx.Single;

import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PreAuthPresenterTest {

    @Mock
    Receipt receipt;

    @Mock
    CardToken cardToken;

    @Mock
    DeviceDna deviceDna;

    @Mock
    JudoApiService apiService;

    @Mock
    TransactionCallbacks transactionCallbacks;

    @InjectMocks
    PreAuthPresenter presenter;

    @Test
    public void shouldPerformPreAuth() {
        when(apiService.preAuth(any(PaymentRequest.class)))
                .thenReturn(Single.<Receipt>just(null));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        presenter.performPreAuth(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference("consumerRef")
                .setJudoId("100915867")
                .build(), null)
                .subscribe();

        verify(apiService).preAuth(any(PaymentRequest.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.preAuth(any(PaymentRequest.class)))
                .thenReturn(Single.<Receipt>error(exception));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        presenter.performPreAuth(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference("consumerRef")
                .setJudoId("100915867")
                .build(), null)
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldPerformTokenPreAuth() {
        when(apiService.tokenPreAuth(any(TokenRequest.class)))
                .thenReturn(Single.<Receipt>just(null));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        when(cardToken.getToken()).thenReturn("cardToken");

        String consumer = "consumerRef";
        presenter.performTokenPreAuth(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setCardToken(cardToken)
                .setConsumerReference(consumer)
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setJudoId("100915867")
                .build(), null)
                .subscribe();

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