package com.judopay;

import android.os.Bundle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class BundleUtil {

    public static Map<String, String> toMap(Bundle bundle) {
        if (bundle != null) {
            Iterator<String> it = bundle.keySet().iterator();
            Map<String, String> map = new HashMap<>();

            while (it.hasNext()) {
                String key = it.next();
                String value = bundle.getString(key);
                map.put(key, value);
            }
        }
        return null;
    }

}