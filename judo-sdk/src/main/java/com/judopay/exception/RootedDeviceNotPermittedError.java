package com.judopay.exception;

/**
 * An exception thrown if the Android device as been detected as having root permissions and the
 * configuration option to block rooted devices has been enabled.
 */
public class RootedDeviceNotPermittedError extends Error {

    public RootedDeviceNotPermittedError() {
        super("Android Root user not permitted");
    }
}