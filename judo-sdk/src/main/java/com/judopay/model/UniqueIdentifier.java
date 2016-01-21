package com.judopay.model;

import java.util.UUID;

class UniqueIdentifier {

    static String generate() {
        return UUID.randomUUID().toString();
    }

}