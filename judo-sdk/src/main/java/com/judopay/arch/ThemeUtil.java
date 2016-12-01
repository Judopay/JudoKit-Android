package com.judopay.arch;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtil {

    public static String getStringAttr(Context context, int attributeName) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeName, value, true);

        return value.string != null ? value.string.toString() : "";
    }

    public static boolean getBooleanAttr(Context context, int attributeName) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeName, value, true);

        return value.data != 0;
    }

}