package com.judopay.card;

import android.support.annotation.Nullable;

import com.judopay.model.Card;

import java.util.Map;

/**
 * A callback for receiving notification that the card entry form was submitted by the user.
 */
public interface CardEntryListener {

    void onSubmit(Card card, @Nullable Map<String, Object> deviceIdentifiers);

}