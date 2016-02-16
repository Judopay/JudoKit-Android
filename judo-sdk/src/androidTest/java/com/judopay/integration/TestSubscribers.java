package com.judopay.integration;

import com.judopay.model.Receipt;

import rx.functions.Action1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TestSubscribers {

    public static Action1<Throwable> fail() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                fail();
            }
        };
    }

    public static Action1<Receipt> assertResponseSuccessful() {
        return new Action1<Receipt>() {
            @Override
            public void call(Receipt receipt) {
                assertThat(receipt.isSuccess(), is(true));
            }
        };
    }

}