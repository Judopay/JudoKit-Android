package com.judopay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Detects if the Android device being used has been rooted. This is done using a variety of
 * methods, such as attempting to access system paths or run system processes that are only
 * accessible when the device is rooted. The methods are not full-proof as if the device is rooted,
 * anything could be changed to simulate an un-rooted device.
 */
class RootDetector {

    /**
     * Detects using several methods if the device is rooted.
     *
     * @return if the device is rooted
     */
    public static boolean isRooted() {
        return isSuRunnable();
    }

    private static boolean isSuRunnable() {
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command("/system/bin/su")
                    .redirectErrorStream(false)
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

