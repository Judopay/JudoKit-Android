package com.judopay.arch;

public interface Scheduler {

    rx.Scheduler mainThread();

    rx.Scheduler backgroundThread();

}