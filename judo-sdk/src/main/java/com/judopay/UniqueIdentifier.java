package com.judopay;

import java.util.UUID;

public class UniqueIdentifier {

    public static String generate() {
        return UUID.randomUUID().toString();
    }

}