package com.judopay.arch;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;

public class ThemeUtil {

    public static String getStringAttr(Context context, Class<?> clazz, int attributeName) {
        TypedArray a = getTypedArray(context, clazz, attributeName);
        String attribute = a.getString(0);
        a.recycle();

        return attribute;
    }

    public static Boolean getBooleanAttr(Context context, Class<?> clazz, int attributeName) {
        TypedArray a = getTypedArray(context, clazz, attributeName);
        boolean attribute = a.getBoolean(0, false);
        a.recycle();

        return attribute;
    }

    private static TypedArray getTypedArray(Context context, Class<?> clazz, int attributeName) {
        int themeName = getThemeName(context, clazz);
        return context.getTheme().obtainStyledAttributes(themeName, new int[]{attributeName});
    }

    private static int getThemeName(Context context, Class<?> clazz) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = clazz.getPackage().getName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);

            return packageInfo.applicationInfo.theme;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
