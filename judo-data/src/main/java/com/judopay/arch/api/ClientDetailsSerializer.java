package com.judopay.arch.api;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.judopay.sheild.JudoShield;

import java.lang.reflect.Type;
import java.util.Map;

public class ClientDetailsSerializer implements JsonSerializer<ClientDetails> {

    private final Context context;

    public ClientDetailsSerializer(Context context) {
        this.context = context;
    }

    @Override
    public JsonElement serialize(ClientDetails src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        Map<String, String> shieldData = JudoShield.getShieldData(this.context);

        for(Map.Entry<String, String> entry : shieldData.entrySet()) {
            jsonObject.addProperty(entry.getKey(), entry.getValue());
        }

        return jsonObject;
    }

}