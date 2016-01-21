package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.model.ThreeDSecureInfo;
import com.judopay.model.TokenTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenPaymentPresenterTest {

    @Mock
    Card card;

    @Mock
    Receipt receipt;

    @Mock
    CardToken cardToken;

    @Mock
    Address cardAddress;

    @Mock
    JudoApiService apiService;

    @Mock
    PaymentFormView paymentFormView;

    @Mock
    ThreeDSecureInfo threeDSecureInfo;

    String consumer = "consumerRef";
    Gson gson = new Gson();
    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformTokenPayment() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler, gson);
        when(apiService.tokenPayment(any(TokenTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performTokenPayment(card, new JudoOptions.Builder()
                .setCardToken(cardToken)
                .setConsumerRef(consumer)
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setJudoId("123456")
                .build());

        verify(paymentFormView).showLoading();
        verify(apiService).tokenPayment(any(TokenTransaction.class));
    }

    @Test
    public void shouldShowWebViewWhenAuthWebPageLoaded() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler, gson);

        presenter.onAuthorizationWebPageLoaded();

        verify(paymentFormView).show3dSecureWebView();
    }

    @Test
    public void shouldFinishWhenSuccessfulReceipt() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler, gson);
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(true);
        when(apiService.threeDSecurePayment(receiptId, threeDSecureInfo)).thenReturn(Observable.just(receipt));

        presenter.onAuthorizationCompleted(threeDSecureInfo, receiptId);

        verify(paymentFormView).finish(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclinedReceipt() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler, gson);
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(false);
        when(apiService.threeDSecurePayment(receiptId, threeDSecureInfo)).thenReturn(Observable.just(receipt));

        presenter.onAuthorizationCompleted(threeDSecureInfo, "123456");

        verify(paymentFormView).showDeclinedMessage(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

}