package com.judopay;

import com.google.gson.Gson;
import com.judopay.arch.Scheduler;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterCardRequest;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegisterCardPresenterTest {

    @Mock
    Card card;

    @Mock
    Receipt receipt;

    @Mock
    Address cardAddress;

    @Mock
    JudoApiService apiService;

    @Mock
    TransactionCallbacks transactionCallbacks;

    private Gson gson = new Gson();
    private String judoId = "123456";
    private String consumer = "consumerRef";
    private Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldRegisterCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);
        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(apiService, times(1)).registerCard(any(RegisterCardRequest.class));
    }

    @Test
    public void showShowLoadingWhenSubmittingCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(transactionCallbacks).showLoading();
    }

    @Test
    public void shouldFinishPaymentFormViewOnSuccess() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);

        when(receipt.isSuccess()).thenReturn(true);
        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.just(receipt));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(transactionCallbacks).onSuccess(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclined() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);

        when(receipt.isSuccess()).thenReturn(false);

        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.just(receipt));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(transactionCallbacks).onDeclined(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldHideLoadingIfReconnectAndPaymentNotInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);
        presenter.reconnect();

        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowLoadingIfReconnectAndPaymentInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        presenter.reconnect();
        verify(transactionCallbacks, times(2)).showLoading();
    }

    @Test
    public void shouldStart3dSecureWebViewIfRequired() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);

        when(receipt.isSuccess()).thenReturn(false);
        when(receipt.is3dSecureRequired()).thenReturn(true);

        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.just(receipt));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(transactionCallbacks).setLoadingText(eq(R.string.redirecting));
        verify(transactionCallbacks).start3dSecureWebView(eq(receipt), eq(presenter));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(transactionCallbacks, apiService, scheduler, gson);
        when(apiService.registerCard(any(RegisterCardRequest.class))).thenReturn(Observable.<Receipt>error(new UnknownHostException()));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(apiService).registerCard(any(RegisterCardRequest.class));
        verify(transactionCallbacks).onConnectionError();
    }

}