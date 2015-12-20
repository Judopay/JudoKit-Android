package com.judopay;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

class BundleUtil {

    public static Map<String, String> toMap(Bundle bundle) {
        Map<String, String> map = new HashMap<>();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                String value = bundle.getString(key);
                map.put(key, value);
            }
        }
        return map;
    }

}