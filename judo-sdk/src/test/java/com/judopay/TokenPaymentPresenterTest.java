package com.judopay;

import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Consumer;
import com.judopay.model.Receipt;
import com.judopay.model.TokenTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.any;
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

    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformTokenPayment() {
        TokenPaymentPresenter presenter = new TokenPaymentPresenter(paymentFormView, apiService, scheduler);
        when(apiService.tokenPayment(any(TokenTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performTokenPayment(card, cardToken, consumer, "123456", "1.99", "GBP", "paymentRef", null, false);

        verify(paymentFormView).showLoading();
        verify(apiService).tokenPayment(any(TokenTransaction.class));
    }
}