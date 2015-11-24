package com.judopay;

public interface Scheduler {

    rx.Scheduler mainThread();

    rx.Scheduler backgroundThread();

}
