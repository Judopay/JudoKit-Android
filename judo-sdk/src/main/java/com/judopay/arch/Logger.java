package com.judopay.arch;

import android.util.Log;

public class Logger {

    public void error(String message, Throwable throwable) {
        Log.e("Judopay", message, throwable);
    }
}
