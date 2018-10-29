package com.judopay.card;

import com.judopay.model.Card;

/**
 * A callback for receiving notification that the card entry form was submitted by the user.
 */
public interface CardEntryListener {

    void onSubmit(Card card);
}
