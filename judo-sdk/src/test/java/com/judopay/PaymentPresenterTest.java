package com.judopay;

import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.payment.Payment;
import com.judopay.payment.Receipt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentPresenterTest {

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
    public void shouldPerformPayment() {
        PaymentPresenter presenter = new PaymentPresenter(paymentFormView, apiService, scheduler);
        when(apiService.payment(any(Payment.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performPayment(card, consumer, "123456", "1.99", "GBP", "paymentRef", null, false);

        verify(apiService).payment(any(Payment.class));
    }

}