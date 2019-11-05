package com.judopay.arch;

import androidx.annotation.Nullable;

public class TextUtil {

    public static boolean isEmpty(@Nullable final CharSequence str) {
        return str == null || str.length() == 0;
    }

}
