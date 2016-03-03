package com.judopay.integration;

import com.judopay.model.Receipt;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

public class RxHelpers {

    public static Action1<Throwable> failOnError() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                fail();
            }
        };
    }

    public static Action1<Receipt> assertTransactionSuccessful() {
        return new Action1<Receipt>() {
            @Override
            public void call(Receipt receipt) {
                assertThat(receipt.isSuccess(), is(true));
            }
        };
    }

    public static <T> Observable.Transformer<T, T> schedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.observeOn(Schedulers.immediate())
                        .subscribeOn(Schedulers.immediate());
            }
        };
    }
}
