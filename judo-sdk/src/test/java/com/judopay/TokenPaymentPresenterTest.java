package com.judopay;

import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Consumer;
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
    Consumer consumer;

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

    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformTokenPayment() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler);
        when(apiService.tokenPayment(any(TokenTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performTokenPayment(card, cardToken, consumer, "123456", "1.99", "GBP", "paymentRef", null, false);

        verify(paymentFormView).showLoading();
        verify(apiService).tokenPayment(any(TokenTransaction.class));
    }

    @Test
    public void shouldShowWebViewWhenAuthWebPageLoaded() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler);

        presenter.onAuthorizationWebPageLoaded();

        verify(paymentFormView).show3dSecureWebView();
    }

    @Test
    public void shouldFinishWhenSuccessfulReceipt() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler);
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(true);
        when(apiService.threeDSecurePayment(receiptId, threeDSecureInfo)).thenReturn(Observable.just(receipt));

        presenter.onAuthorizationCompleted(threeDSecureInfo, receiptId);

        verify(paymentFormView).finish(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclinedReceipt() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler);
        String receiptId = "123456";

        when(receipt.isSuccess()).thenReturn(false);
        when(apiService.threeDSecurePayment(receiptId, threeDSecureInfo)).thenReturn(Observable.just(receipt));

        presenter.onAuthorizationCompleted(threeDSecureInfo, "123456");

        verify(paymentFormView).showDeclinedMessage(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

}