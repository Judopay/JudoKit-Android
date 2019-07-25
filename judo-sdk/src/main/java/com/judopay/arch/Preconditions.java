package com.judopay.arch;

public final class Preconditions {

    private Preconditions() {
        throw new UnsupportedOperationException();
    }

    public static <T> T checkNotNull(final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static void checkNotEmpty(final CharSequence text) {
        checkNotNull(text);

        if (TextUtil.isEmpty(text)) {
            throw new IllegalArgumentException();
        }
    }

}