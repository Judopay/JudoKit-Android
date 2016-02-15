package com.judopay.ui.util;

import android.app.Activity;
import android.content.Intent;

import java.lang.reflect.Field;

public class ActivityUtil {

    public static Integer resultCode(Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mResultCode");
            field.setAccessible(true);
            return (Integer) field.get(activity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Intent getResultIntent(Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mResultData");
            field.setAccessible(true);
            return (Intent) field.get(activity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
