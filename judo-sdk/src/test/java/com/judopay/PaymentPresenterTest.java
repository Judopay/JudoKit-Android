package com.judopay;

import com.judopay.customer.Address;
import com.judopay.customer.Card;
import com.judopay.payment.PaymentTransaction;
import com.judopay.payment.Receipt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Mock
    Payment payment;

    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformPayment() {
        when(payment.getAmount()).thenReturn("1.99");
        when(payment.getCurrency()).thenReturn("GBP");
        when(payment.getJudoId()).thenReturn("123456");

        when(payment.getConsumer()).thenReturn(consumer);

        PaymentPresenter presenter = new PaymentPresenter(paymentFormView, apiService, scheduler, payment);

        presenter.performApiCall(card, consumer);

        verify(apiService).payment(any(PaymentTransaction.class));
    }

}