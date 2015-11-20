package com.judopay;

import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.payment.Receipt;
import com.judopay.register.RegisterTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
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
    Consumer consumer;

    @Mock
    Address cardAddress;

    @Mock
    JudoApiService apiService;

    @Mock
    PaymentFormView paymentFormView;

    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldRegisterCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);

        presenter.performApiCall(card, consumer);

        verify(apiService, times(1)).registerCard(any(RegisterTransaction.class));
    }

    @Test
    public void showShowLoadingWhenSubmittingCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.onSubmit(card, consumer, false);

        verify(paymentFormView).showLoading();
    }

    @Test
    public void shouldFinishPaymentFormViewOnSuccess() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);

        when(receipt.isSuccess()).thenReturn(true);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.just(receipt));
        presenter.onSubmit(card, consumer, false);

        verify(paymentFormView).finish(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclined() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);

        when(receipt.isSuccess()).thenReturn(false);

        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.just(receipt));
        presenter.onSubmit(card, consumer, false);

        verify(paymentFormView).showDeclinedMessage(eq(receipt));
        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldHideLoadingIfReconnectAndPaymentNotInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);
        presenter.reconnect();

        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldShowLoadingIfReconnectAndPaymentInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.onSubmit(card, consumer, false);
        presenter.reconnect();

        verify(paymentFormView, times(2)).showLoading();
    }

    @Test
    public void shouldStart3dSecureWebViewIfRequired() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);

        when(receipt.isSuccess()).thenReturn(false);
        when(receipt.is3dSecureRequired()).thenReturn(true);

        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.just(receipt));
        presenter.onSubmit(card, consumer, true);

        verify(paymentFormView).setLoadingText(eq(R.string.redirecting));
        verify(paymentFormView).start3dSecureWebView(eq(receipt));
    }

    @Test
    public void shouldDeclineIf3dSecureRequiredButNotEnabled() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(paymentFormView, apiService, scheduler);

        when(receipt.isSuccess()).thenReturn(false);
        when(receipt.is3dSecureRequired()).thenReturn(true);

        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.just(receipt));
        presenter.onSubmit(card, consumer, false);
        verify(paymentFormView).showDeclinedMessage(eq(receipt));
        verify(paymentFormView, never()).start3dSecureWebView(eq(receipt));
    }

}