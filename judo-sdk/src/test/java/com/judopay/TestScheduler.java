package com.judopay;

import rx.schedulers.Schedulers;

public class TestScheduler implements Scheduler {

    @Override
    public rx.Scheduler mainThread() {
        return Schedulers.immediate();
    }

    @Override
    public rx.Scheduler backgroundThread() {
        return Schedulers.immediate();
    }

}