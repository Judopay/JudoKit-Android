package com.judopay;

import com.judopay.api.JudoApiServiceFactory;
import com.judopay.arch.Logger;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.SaveCardRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.HttpException;
import rx.Observable;
import rx.Single;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.Single.just;

@RunWith(MockitoJUnitRunner.class)
public class SaveCardPresenterTest {

    @Mock
    private Receipt receipt;

    @Mock
    private Address cardAddress;

    @Mock
    private DeviceDna deviceDna;

    @SuppressWarnings("unused")
    @Mock
    private Logger logger;

    @Mock
    private JudoApiService apiService;

    @Mock
    private TransactionCallbacks transactionCallbacks;

    @InjectMocks
    private SaveCardPresenter presenter;

    private String judoId = "100407196";
    private String consumer = "consumerRef";

    @Test
    public void shouldSaveCard() {
        when(apiService.saveCard(any(SaveCardRequest.class)))
                .thenReturn(Single.just(null));

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
                .subscribe();

        verify(apiService, times(1)).saveCard(any(SaveCardRequest.class));
    }

    @Test
    public void showShowLoadingWhenSubmittingCard() {
        when(apiService.saveCard(any(SaveCardRequest.class)))
                .thenReturn(Single.just(null));

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
                .subscribe();

        verify(transactionCallbacks).showLoading();
    }

    @Test
    public void shouldFinishPaymentFormViewOnSuccess() {
        when(receipt.isSuccess())
                .thenReturn(true);

        when(apiService.saveCard(any(SaveCardRequest.class)))
                .thenReturn(just(receipt));

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onSuccess(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclined() {
        when(receipt.isSuccess()).thenReturn(false);

        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(just(receipt));

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
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

        when(apiService.saveCard(any(SaveCardRequest.class)))
                .thenReturn(response);

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(card, new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
                .subscribe();

        presenter.reconnect();

        verify(transactionCallbacks, times(2)).showLoading();
    }

    @Test
    public void shouldStart3dSecureWebViewIfRequired() {
        when(receipt.isSuccess()).thenReturn(false);
        when(receipt.is3dSecureRequired()).thenReturn(true);

        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(just(receipt));

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).setLoadingText(eq(R.string.redirecting));
        verify(transactionCallbacks).start3dSecureWebView(eq(receipt), eq(presenter));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        Buffer buffer = new Buffer();
        buffer.writeUtf8(JudoApiServiceFactory.getGson().toJson(new Receipt()));

        RealResponseBody responseBody = new RealResponseBody("application/json", buffer.size(), buffer);
        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.error(exception));

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
                .subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.error(new UnknownHostException()));

        when(deviceDna.send(any()))
                .thenReturn(just(randomUUID().toString()));

        presenter.performSaveCard(getCard(), new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build(), new HashMap<>())
                .subscribe(presenter.callback(), presenter.error());

        verify(apiService).saveCard(any(SaveCardRequest.class));
        verify(transactionCallbacks).onConnectionError();
    }

    private Card getCard() {
        return new Card.Builder()
                .setCardNumber("4976000000003436")
                .setSecurityCode("456")
                .setExpiryDate("12/21")
                .build();
    }
}
