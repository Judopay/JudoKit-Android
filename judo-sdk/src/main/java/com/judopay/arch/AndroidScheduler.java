package com.judopay.arch;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AndroidScheduler implements Scheduler {

    @Override
    public rx.Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public rx.Scheduler backgroundThread() {
        return Schedulers.io();
    }
}
