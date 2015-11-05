package com.judopay.sheild;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class JudoShield {

    private static final String OS = "OS";
    private static final String DEVICE_MODEL = "deviceModel";
    private static final String COUNTRY_CODE = "countryCode";
    private static final String K_DEVICE_ID = "kDeviceID";
    private static final String V_DEVICE_ID = "vDeviceID";

    public static final String SHARED_PREFS_NAME = "JudoShield";
    public static final String V_DEVICE_ID_KEY = "Judo-vDeviceId";

    public Map<String, String> getShieldData(Context context) {
        Map<String, String> data = new HashMap<>();

        if (context != null) {
            data.put(K_DEVICE_ID, getAndroidId(context));
            data.put(V_DEVICE_ID, getVDeviceId(context));

            data.put(OS, getOsVersion());
            data.put(DEVICE_MODEL, Build.MODEL);

            data.put(COUNTRY_CODE, CountryCodes.getByLocale(Locale.getDefault()));
        }
        return data;
    }

    private String getVDeviceId(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        if(sharedPrefs.contains(V_DEVICE_ID_KEY)) {
            return sharedPrefs.getString(V_DEVICE_ID, null);
        } else {
            String vDeviceId = UUID.randomUUID().toString();
            sharedPrefs.edit().putString(V_DEVICE_ID, vDeviceId).apply();

            return vDeviceId;
        }
    }

    private String getOsVersion() {
        return String.format("Android OS %s", Build.VERSION.RELEASE);
    }

    private String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
