package com.judopay;

interface Scheduler {

    rx.Scheduler mainThread();

    rx.Scheduler backgroundThread();

}