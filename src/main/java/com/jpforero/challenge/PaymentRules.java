package com.jpforero.challenge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static com.jpforero.challenge.Constants.PAYMENT_RULES;

public class PaymentRules {

    private int maxLimit;

    public int getMaxLimit() {
        return maxLimit;
    }

    @Override
    public String toString() {
        JsonObject response = new JsonObject();
        JsonElement jsonElement = Tools.gson.toJsonTree(this);
        response.add(PAYMENT_RULES, jsonElement);

        return response.toString();
    }
}
