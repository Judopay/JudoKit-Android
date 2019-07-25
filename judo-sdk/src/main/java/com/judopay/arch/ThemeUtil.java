package com.judopay.arch;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.judopay.R;

public class ThemeUtil {

    public static String getStringAttr(final Context context, final int attributeName) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeName, value, true);

        return value.string != null ? value.string.toString() : "";
    }

    public static boolean getBooleanAttr(final Context context, final int attributeName) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attributeName, value, true);

        return value.data != 0;
    }

    public static int getColorAttr(final Context context, final int attribute) {
        if (context == null) {
            return 0;
        }

        TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(attribute, value, true)) {
            return value.data;
        }

        return ContextCompat.getColor(context, R.color.judo_white_90);
    }
}
