package com.judopay;

import com.google.gson.JsonElement;
import com.judopay.arch.Logger;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterCardRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.Headers;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Single;

import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.Single.just;

@RunWith(MockitoJUnitRunner.class)
public class RegisterCardPresenterTest {

    @Mock
    Receipt receipt;

    @Mock
    Address cardAddress;

    @Mock
    DeviceDna deviceDna;

    @Mock
    Logger logger;

    @Mock
    JudoApiService apiService;

    @Mock
    TransactionCallbacks transactionCallbacks;

    @InjectMocks
    RegisterCardPresenter presenter;

    private String judoId = "100407196";
    private String consumer = "consumerRef";

    @Test
    public void shouldRegisterCard() {
        when(apiService.registerCard(any(RegisterCardRequest.class)))
                .thenReturn(Single.<Receipt>just(null));

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe();

        verify(apiService, times(1)).registerCard(any(RegisterCardRequest.class));
    }

    @Test
    public void showShowLoadingWhenSubmittingCard() {
        when(apiService.registerCard(any(RegisterCardRequest.class)))
                .thenReturn(Single.<Receipt>just(null));

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe();

        verify(transactionCallbacks).showLoading();
    }

    @Test
    public void shouldFinishPaymentFormViewOnSuccess() {
        when(receipt.isSuccess())
                .thenReturn(true);

        when(apiService.registerCard(any(RegisterCardRequest.class)))
                .thenReturn(just(receipt));

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onSuccess(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclined() {
        when(receipt.isSuccess()).thenReturn(false);

        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(just(receipt));

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onDeclined(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldHideLoadingIfReconnectAndPaymentNotInProgress() {
        presenter.reconnect();
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowLoadingIfReconnectAndPaymentInProgress() {
        Card card = new Card.Builder()
                .setCardNumber("4976000000003436")
                .setSecurityCode("452")
                .setExpiryDate("12/20")
                .setAddress(cardAddress)
                .build();

        // create a Receipt response that won't complete before we attempt to reconnect to the presenter;
        Single<Receipt> response = Observable.<Receipt>never().toSingle();

        when(apiService.registerCard(any(RegisterCardRequest.class)))
                .thenReturn(response);

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(card, new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe();

        presenter.reconnect();

        verify(transactionCallbacks, times(2)).showLoading();
    }

    @Test
    public void shouldStart3dSecureWebViewIfRequired() {
        when(receipt.isSuccess()).thenReturn(false);
        when(receipt.is3dSecureRequired()).thenReturn(true);

        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(just(receipt));

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).setLoadingText(eq(R.string.redirecting));
        verify(transactionCallbacks).start3dSecureWebView(eq(receipt), eq(presenter));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Single.<Receipt>error(exception));

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Single.<Receipt>error(new UnknownHostException()));

        when(deviceDna.send(anyMapOf(String.class, JsonElement.class)))
                .thenReturn(just(randomUUID().toString()));

        presenter.performRegisterCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<String, Object>())
                .subscribe(presenter.callback(), presenter.error());

        verify(apiService).registerCard(any(RegisterCardRequest.class));
        verify(transactionCallbacks).onConnectionError();
    }

    public Card getCard() {
        return new Card.Builder()
                .setCardNumber("4976000000003436")
                .setSecurityCode("456")
                .setExpiryDate("12/21")
                .build();
    }

}