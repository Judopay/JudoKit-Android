package com.judopay;

import com.judopay.api.JudoApiService;
import com.judopay.api.factory.JudoApiServiceFactory;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.api.model.request.PaymentRequest;
import com.judopay.api.model.response.Receipt;
import com.judopay.model.TokenRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Single;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import retrofit2.HttpException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PreAuthPresenterTest {

    @Mock
    private CardToken cardToken;

    @Mock
    private JudoApiService apiService;

    @Mock
    private TransactionCallbacks transactionCallbacks;

    @InjectMocks
    private PreAuthPresenter presenter;

    @Test
    public void shouldPerformPreAuth() {
        when(apiService.preAuth(any(PaymentRequest.class))).thenReturn(Single.just(new Receipt()));

        presenter.performPreAuth(getCard(), getJudo(null)).subscribe();

        verify(apiService).preAuth(any(PaymentRequest.class));
    }

    @Test
    public void shouldReturnReceiptWhenBadRequest() {
        Buffer buffer = new Buffer();
        buffer.writeUtf8(JudoApiServiceFactory.getGson().toJson(new Receipt()));

        RealResponseBody responseBody = new RealResponseBody("application/json", buffer.size(), buffer);
        HttpException exception = new HttpException(retrofit2.Response.error(400, responseBody));

        when(apiService.preAuth(any(PaymentRequest.class))).thenReturn(Single.error(exception));

        presenter.performPreAuth(getCard(), getJudo(null)).subscribe(presenter.callback(), presenter.error());

        verify(transactionCallbacks).onError(any(Receipt.class));
    }

    @Test
    public void shouldPerformTokenPreAuth() {
        when(apiService.tokenPreAuth(any(TokenRequest.class))).thenReturn(Single.just(new Receipt()));

        when(cardToken.getToken()).thenReturn("cardToken");

        presenter.performTokenPreAuth(getCard(), getJudo(cardToken)).subscribe();

        verify(transactionCallbacks).showLoading();
        verify(apiService).tokenPreAuth(any(TokenRequest.class));
    }

    private Judo getJudo(CardToken cardToken) {
        return new Judo.Builder("apiToken", "apiSecret")
                .setCardToken(cardToken)
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setConsumerReference("consumerRef")
                .setJudoId("100915867")
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
