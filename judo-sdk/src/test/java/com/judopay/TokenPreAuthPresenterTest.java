package com.judopay;

import com.google.gson.Gson;
import com.judopay.model.Card;
import com.judopay.model.CardToken;
import com.judopay.model.Currency;
import com.judopay.model.Receipt;
import com.judopay.model.TokenRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenPreAuthPresenterTest {

    @Mock
    Card card;

    @Mock
    Receipt receipt;

    @Mock
    CardToken cardToken;

    @Mock
    JudoApiService apiService;

    @Mock
    TransactionCallbacks transactionCallbacks;

    @Test
    public void shouldPerformTokenPreAuth() {
        TokenPreAuthPresenter presenter = new TokenPreAuthPresenter(transactionCallbacks, apiService, new TestScheduler(), new Gson());
        when(apiService.tokenPreAuth(any(TokenRequest.class))).thenReturn(Observable.<Receipt>empty());

        String consumer = "consumerRef";
        presenter.performTokenPreAuth(card, new JudoOptions.Builder()
                .setCardToken(cardToken)
                .setConsumerRef(consumer)
                .setAmount("1.99")
                .setCurrency(Currency.GBP)
                .setJudoId("123456")
                .build());

        verify(transactionCallbacks).showLoading();
        verify(apiService).tokenPreAuth(any(TokenRequest.class));
    }

}