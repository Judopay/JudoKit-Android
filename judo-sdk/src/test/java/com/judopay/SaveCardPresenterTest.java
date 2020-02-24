package com.judopay;

import com.judopay.api.JudoApiService;
import com.judopay.api.factory.JudoApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.api.model.response.Receipt;
import com.judopay.api.model.request.SaveCardRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.HttpException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaveCardPresenterTest {

    @Mock
    private Receipt receipt;

    @Mock
    private JudoApiService apiService;

    @Mock
    private TransactionCallbacks transactionCallbacks;

    @InjectMocks
    private SaveCardPresenter presenter;

    @Test
    public void shouldCallAPI() {
        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.just(new Receipt()));

        presenter.performSaveCard(getCard(), getJudo()).subscribe();

        verify(apiService, times(1)).saveCard(any(SaveCardRequest.class));
    }

    @Test
    public void showShowLoadingWhenSubmittingCard() {
        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.just(new Receipt()));

        presenter.performSaveCard(getCard(), getJudo()).subscribe();

        verify(transactionCallbacks).showLoading();
    }

    @Test
    public void shouldFinishPaymentFormViewOnSuccess() {
        when(receipt.isSuccess()).thenReturn(true);

        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.just(receipt));

        presenter.performSaveCard(getCard(), getJudo()).subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onSuccess(eq(receipt));
        verify(transactionCallbacks).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclined() {
        when(receipt.isSuccess()).thenReturn(false);

        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.just(receipt));

        presenter.performSaveCard(getCard(), getJudo()).subscribe(presenter.callback(), presenter.error());

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
        // create a Receipt response that won't complete before we attempt to reconnect to the presenter;
        Single<Receipt> response = Observable.<Receipt>never().singleOrError();

        when(apiService.saveCard(any(SaveCardRequest.class)))
                .thenReturn(response);

        presenter.performSaveCard(getCard(), getJudo()).subscribe();

        presenter.reconnect();

        verify(transactionCallbacks, times(2)).showLoading();
    }

    @Test
    public void shouldStart3dSecureWebViewIfRequired() {
        when(receipt.isSuccess()).thenReturn(false);
        when(receipt.is3dSecureRequired()).thenReturn(true);

        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.just(receipt));

        presenter.performSaveCard(getCard(), getJudo()).subscribe(presenter.callback(), presenter.error());

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

        presenter.performSaveCard(getCard(), getJudo()).subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldShowConnectionErrorDialog() {
        when(apiService.saveCard(any(SaveCardRequest.class))).thenReturn(Single.error(new UnknownHostException()));

        presenter.performSaveCard(getCard(), getJudo()).subscribe(presenter.callback(), presenter.error());

        verify(apiService).saveCard(any(SaveCardRequest.class));
        verify(transactionCallbacks).onConnectionError();
    }

    private Judo getJudo() {
        String judoId = "100407196";
        String consumer = "consumerRef";
        return new Judo.Builder("apiToken", "apiSecret")
                .setJudoId(judoId)
                .setConsumerReference(consumer)
                .build();
    }

    private Card getCard() {
        return new Card.Builder()
                .setCardNumber("4976000000003436")
                .setSecurityCode("456")
                .setExpiryDate("12/21")
                .build();
    }
}
