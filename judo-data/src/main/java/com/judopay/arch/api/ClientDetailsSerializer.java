package com.judopay.arch.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.judopay.model.ClientDetails;
import com.judopay.shield.JudoShield;

import java.lang.reflect.Type;
import java.util.Map;

public class ClientDetailsSerializer implements JsonSerializer<ClientDetails> {

    private final Context context;

    public ClientDetailsSerializer(Context context) {
        this.context = context;
    }

    @Override
    public JsonElement serialize(ClientDetails src, Type typeOfSrc, JsonSerializationContext context) {
        Map<String, String> shieldData = JudoShield.getShieldData(this.context);

        return new Gson().toJsonTree(shieldData);
    }

}