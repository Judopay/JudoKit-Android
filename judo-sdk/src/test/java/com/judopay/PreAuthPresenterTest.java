package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Address;
import com.judopay.model.Card;
import com.judopay.model.PaymentTransaction;
import com.judopay.model.Receipt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PreAuthPresenterTest {

    @Mock
    Card card;

    @Mock
    Receipt receipt;

    @Mock
    Address cardAddress;

    @Mock
    JudoApiService apiService;

    @Mock
    PaymentFormView paymentFormView;

    Gson gson = new Gson();
    Scheduler scheduler = new TestScheduler();

    @Test
    public void shouldPerformPreAuth() {
        PreAuthPresenter presenter = new PreAuthPresenter(paymentFormView, apiService, scheduler, gson);
        when(apiService.preAuth(any(PaymentTransaction.class))).thenReturn(Observable.<Receipt>empty());

        presenter.performPreAuth(card, "consumerRef", "123456", "1.99", "GBP", null, false);

        verify(apiService).preAuth(any(PaymentTransaction.class));
    }

}