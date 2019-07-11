package com.judopay;

import com.judopay.model.Currency;

import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class TestUtil {

    public static final String API_TOKEN = "Izx9omsBR15LatAl";
    public static final String API_SECRET = "b5787124845533d8e68d12a586fa3713871b876b528600ebfdc037afec880cd6";
    public static final String JUDO_ID_IRIDIUM = "100915867";
    public static final String JUDO_ID_CYBERSOURCE = "100579473";

    public static Judo getJudo() {
        return new Judo.Builder()
                .setEnvironment(Judo.SANDBOX)
                .setApiToken(API_TOKEN)
                .setApiSecret(API_SECRET)
                .setJudoId(JUDO_ID_IRIDIUM)
                .setCurrency(Currency.GBP)
                .setAmount("0.10")
                .setConsumerReference(UUID.randomUUID().toString())
                .build();
    }

    public static Judo getJudoWithCyberSource() {
        return new Judo.Builder()
                .setEnvironment(Judo.SANDBOX)
                .setApiToken(API_TOKEN)
                .setApiSecret(API_SECRET)
                .setJudoId(JUDO_ID_CYBERSOURCE)
                .setCurrency(Currency.GBP)
                .setAmount("0.10")
                .setConsumerReference(UUID.randomUUID().toString())
                .build();
    }
}
