package com.judopay.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Detects if the Android device being used has been rooted. This is done using a variety of
 * methods, such as attempting to access system paths or run system processes that are only
 * accessible when the device is rooted. The methods are not full-proof as if the device is rooted,
 * anything could be changed to simulate an un-rooted device.
 * Credit to Kevin Kowalewski for providing the root detection methods used.
 */
public class RootDetector {

    /**
     * Detects using several methods if the device is rooted.
     * @return if the device is rooted
     */
    public static boolean isRooted() {
        return isTestKeysPresent() || isSystemPathAccessible();
    }

    private static boolean isTestKeysPresent() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean isSystemPathAccessible() {
        String[] paths = {"/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"};

        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean isSystemCommandExecutable() {
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command("/system/bin/ping", "android.com")
                    .redirectErrorStream(true)
                    .start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (IOException ignore) {
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

}

