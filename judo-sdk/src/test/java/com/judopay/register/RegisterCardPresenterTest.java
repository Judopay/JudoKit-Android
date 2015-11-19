package com.judopay.register;

import com.judopay.Consumer;
import com.judopay.JudoApiService;
import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.payment.Receipt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    Consumer consumer;

    @Mock
    Address cardAddress;

    @Mock
    JudoApiService apiService;

    @Mock
    PaymentFormView paymentFormView;

    @Test
    public void shouldRegisterCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(consumer, paymentFormView, apiService);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.onSubmit(card);

        verify(apiService, times(1)).registerCard(any(RegisterTransaction.class));
    }

    @Test
    public void shouldHideLoadingOnComplete() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(consumer, paymentFormView, apiService);

        presenter.onCompleted();

        verify(paymentFormView).hideLoading();
    }

    @Test
    public void showShowLoadingWhenSubmittingCard() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(consumer, paymentFormView, apiService);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.onSubmit(card);

        verify(paymentFormView).showLoading();
    }

    @Test
    public void shouldFinishPaymentFormViewOnSuccess() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(consumer, paymentFormView, apiService);

        when(receipt.isSuccess()).thenReturn(true);
        presenter.onNext(receipt);

        verify(paymentFormView).finish(eq(receipt));
    }

    @Test
    public void shouldShowDeclinedMessageWhenDeclined() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(consumer, paymentFormView, apiService);

        when(receipt.isSuccess()).thenReturn(false);

        presenter.onNext(receipt);

        verify(paymentFormView).showDeclinedMessage(eq(receipt));
    }

    @Test
    public void shouldHideLoadingIfReconnectAndPaymentNotInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(consumer, paymentFormView, apiService);
        presenter.reconnect();

        verify(paymentFormView).hideLoading();
    }

    @Test
    public void shouldShowLoadingIfReconnectAndPaymentInProgress() {
        RegisterCardPresenter presenter = new RegisterCardPresenter(consumer, paymentFormView, apiService);

        when(card.getCardAddress()).thenReturn(cardAddress);
        when(apiService.registerCard(any(RegisterTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.onSubmit(card);

        presenter.reconnect();

        verify(paymentFormView, times(2)).showLoading();
    }

}