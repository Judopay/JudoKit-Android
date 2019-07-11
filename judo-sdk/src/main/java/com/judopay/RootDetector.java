package com.judopay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class RootDetector {

    static boolean isRooted() {
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
