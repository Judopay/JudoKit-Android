package com.judopay.arch;

import android.support.annotation.Nullable;

public class TextUtil {

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

}
