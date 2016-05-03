package com.judopay.error;

import android.view.View;

import java.util.Locale;

public class InvalidViewTypeInLayout extends Error {

    public InvalidViewTypeInLayout(Class<? extends View> viewClass) {
        super(String.format(Locale.US, "View class %s is not allowed inside custom layout", viewClass.getName()));
    }

}