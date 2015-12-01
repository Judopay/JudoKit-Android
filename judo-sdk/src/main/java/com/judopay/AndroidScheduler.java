package com.judopay;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class AndroidScheduler implements Scheduler {

    @Override
    public rx.Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public rx.Scheduler backgroundThread() {
        return Schedulers.newThread();
    }
}
