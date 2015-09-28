package com.judopay.payment;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {

    @Test
    public void shouldMakePaymentWithTransaction() {
        PaymentsApiService paymentsApiService = mock(PaymentsApiService.class);
        Transaction transaction = mock(Transaction.class);
        PaymentResponse paymentResponse = new PaymentResponse();

        when(paymentsApiService.payment(transaction))
                .thenReturn(paymentResponse);

        PaymentService paymentService = new PaymentService(paymentsApiService);
        PaymentResponse actualPaymentResponse = paymentService.payment(transaction);

        assertThat(actualPaymentResponse, equalTo(paymentResponse));
    }

}