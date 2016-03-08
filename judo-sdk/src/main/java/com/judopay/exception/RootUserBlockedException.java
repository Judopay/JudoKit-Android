package com.judopay.exception;

/**
 * An exception thrown if the Android device as been detected as having root permissions and the
 * configuration option to block rooted devices has been enabled.
 */
public class RootUserBlockedException extends RuntimeException {

    public RootUserBlockedException() {
        super("Android Root user not permitted");
    }
}