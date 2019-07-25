package com.judopay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class RootDetector {

    static boolean isRooted() {
        return isSuRunnable();
    }

    @SuppressWarnings("RV_DONT_JUST_NULL_CHECK_READLINE ")
    private static boolean isSuRunnable() {
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command("/system/bin/su")
                    .redirectErrorStream(false)
                    .start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            final boolean isSuRunnable = reader.readLine() != null;
            reader.close();
            return isSuRunnable;
        } catch (IOException ignore) {
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }
}
