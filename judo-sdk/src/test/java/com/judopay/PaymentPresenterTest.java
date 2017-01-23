package com.judopay;

import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.CardVerificationResult;
import com.judopay.model.Currency;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.UnknownHostException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.adapter.rxjava.HttpException;
import rx.Single;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
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
    Map<String, Object> fieldMetaData;

    @Mock
    DeviceDna deviceDna;

    @Mock
    JudoApiService apiService;

    @Mock
    TransactionCallbacks transactionCallbacks;

    @InjectMocks
    PaymentPresenter presenter;

    @Test
    public void shouldPerformPayment() {
        when(apiService.payment(any(PaymentRequest.class)))
                .thenReturn(Single.just(receipt));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        Receipt result = presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference("consumerRef")
                .setJudoId("100915867")
                .build(), fieldMetaData)
                .toBlocking()
                .value();

        assertThat(result, equalTo(receipt));
        verify(apiService).payment(any(PaymentRequest.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        when(apiService.payment(any(PaymentRequest.class)))
                .thenReturn(Single.<Receipt>error(new UnknownHostException()));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference("consumerRef")
                .setJudoId("100915867")
                .build(), fieldMetaData)
                .subscribe(presenter.callback(), presenter.error());

        verify(apiService).payment(any(PaymentRequest.class));
        verify(transactionCallbacks).onConnectionError();
    }

    @Test
    public void shouldReturnReceiptWhenHttpException() {
        Buffer buffer = new Buffer();

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), buffer);
        HttpException exception = new HttpException(retrofit2.Response.error(404, responseBody));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        when(apiService.payment(any(PaymentRequest.class)))
                .thenReturn(Single.<Receipt>error(exception));

        presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference("consumerRef")
                .setJudoId("100915867")
                .build(), fieldMetaData)
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());
        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        when(apiService.payment(any(PaymentRequest.class)))
                .thenReturn(Single.<Receipt>error(exception));

        presenter.performPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference("consumerRef")
                .setJudoId("100915867")
                .build(), fieldMetaData)
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldPerformTokenPayment() {
        when(apiService.tokenPayment(any(TokenRequest.class)))
                .thenReturn(Single.<Receipt>just(null));

        when(deviceDna.send(anyMapOf(String.class, Object.class)))
                .thenReturn(Single.just(randomUUID().toString()));

        when(cardToken.getToken()).thenReturn("cardToken");

        String consumer = "consumerRef";
        presenter.performTokenPayment(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setCardToken(cardToken)
                .setConsumerReference(consumer)
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setJudoId("100915867")
                .build(), null)
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).showLoading();
        verify(apiService).tokenPayment(any(TokenRequest.class));
    }

    @Test
    public void shouldFinishWhenSuccessfulReceipt() {
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(true);
        when(apiService.complete3dSecure(receiptId, cardVerificationResult)).thenReturn(Single.just(receipt));

        presenter.onAuthorizationCompleted(cardVerificationResult, receiptId)
                .subscribe();

        verify(transactionCallbacks).onSuccess(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclinedReceipt() {
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(false);
        when(apiService.complete3dSecure(receiptId, cardVerificationResult)).thenReturn(Single.just(receipt));

        presenter.onAuthorizationCompleted(cardVerificationResult, "123456")
                .subscribe();

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