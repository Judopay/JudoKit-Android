package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.Receipt;
import com.judopay.model.RegisterTransaction;
import com.judopay.payment.form.JudoOptions;
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
    PaymentFormView paymentFormView;

    Gson gson = new Gson();
    String judoId = "123456";
    String consumer = "consumerRef";
    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldRegisterCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(apiService, times(1)).registerCard(any(RegisterTransaction.class));
    }

    @Test
    public void showShowLoadingWhenSubmittingCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(paymentFormView).showLoading();
    }

    @Test
    public void shouldFinishPaymentFormViewOnSuccess() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);

        when(receipt.isSuccess()).thenReturn(true);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.just(receipt));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(paymentFormView).finish(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclined() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);

        when(receipt.isSuccess()).thenReturn(false);

        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.just(receipt));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(paymentFormView).showDeclinedMessage(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldHideLoadingIfReconnectAndPaymentNotInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);
        presenter.reconnect();

        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldShowLoadingIfReconnectAndPaymentInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        presenter.reconnect();
        verify(paymentFormView, times(2)).showLoading();
    }

    @Test
    public void shouldStart3dSecureWebViewIfRequired() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);

        when(receipt.isSuccess()).thenReturn(false);
        when(receipt.is3dSecureRequired()).thenReturn(true);

        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.just(receipt));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(paymentFormView).setLoadingText(eq(R.string.redirecting));
        verify(paymentFormView).start3dSecureWebView(eq(receipt), eq(presenter));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);

        RealResponseBody responseBody = new RealResponseBody(Headers.of("SdkVersion", "5.0"), new Buffer());

        HttpException exception = new HttpException(retrofit.Response.error(400, responseBody));

        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>error(exception));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(paymentFormView).showDeclinedMessage(any(Receipt.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler, gson);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>error(new UnknownHostException()));

        presenter.performRegisterCard(card, new JudoOptions.Builder()
                .setJudoId(judoId)
                .setConsumerRef(consumer)
                .build());

        verify(apiService).registerCard(any(RegisterTransaction.class));
        verify(paymentFormView).showConnectionErrorDialog();
    }

}