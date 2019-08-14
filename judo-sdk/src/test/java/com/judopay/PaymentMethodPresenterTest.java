package com.judopay;


import com.judopay.model.PaymentMethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.EnumSet;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodPresenterTest {

    @Mock
    private PaymentMethodView view;

    @InjectMocks
    private PaymentMethodPresenter presenter;


    @Test
    public void shouldCallDisplayMethodViewOnce() {
        EnumSet<PaymentMethod> paymentMethod = EnumSet.of(PaymentMethod.CREATE_PAYMENT);

        presenter.setPaymentMethod(paymentMethod);
        verify(view).displayPaymentMethodView(PaymentMethod.CREATE_PAYMENT.getViewId());
        verify(view, times(1)).displayPaymentMethodView(anyInt());
    }

    @Test
    public void shouldCallDisplayMethodViewAndSetUpGPAY() {
        EnumSet<PaymentMethod> paymentMethod = EnumSet.of(PaymentMethod.CREATE_PAYMENT, PaymentMethod.GPAY_PAYMENT);

        presenter.setPaymentMethod(paymentMethod);
        verify(view).displayPaymentMethodView(anyInt());
        verify(view).setUpGPayButton();
    }

    @Test
    public void shouldCallDisplayAllPaymentMethodsWhenEmptyList() {
        EnumSet<PaymentMethod> paymentMethod = EnumSet.noneOf(PaymentMethod.class);

        presenter.setPaymentMethod(paymentMethod);
        verify(view, never()).displayPaymentMethodView(anyInt());
        verify(view).displayAllPaymentMethods();
    }

    @Test
    public void shouldCallDisplayAllPaymentMethodsWhenNullList() {
        presenter.setPaymentMethod(null);
        verify(view, never()).displayPaymentMethodView(anyInt());
        verify(view).displayAllPaymentMethods();
    }
}
