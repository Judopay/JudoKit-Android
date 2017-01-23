package com.judopay;

import com.judopay.model.Currency;

import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class TestUtil {

    public static final String API_TOKEN = "<API TOKEN>";
    public static final String API_SECRET = "<API SECRET>";
    public static final String JUDO_ID = "<JUDO ID>";

    public static Judo getJudo() {
        return new Judo.Builder()
                .setEnvironment(Judo.SANDBOX)
                .setApiToken(API_TOKEN)
                .setApiSecret(API_SECRET)
                .setJudoId(JUDO_ID)
                .setCurrency(Currency.GBP)
                .setAmount("0.10")
                .setConsumerReference(UUID.randomUUID().toString())
                .build();
    }
}