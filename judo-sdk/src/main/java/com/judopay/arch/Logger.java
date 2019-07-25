package com.judopay.arch;

import android.util.Log;

public class Logger {

    public void error(final String message, final Throwable throwable) {
        Log.e("Judopay", message, throwable);
    }
}
