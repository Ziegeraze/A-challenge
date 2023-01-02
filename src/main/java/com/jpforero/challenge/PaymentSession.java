package com.jpforero.challenge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jpforero.challenge.Constants.*;

public class PaymentSession {


    private int paymentId;
    private int availableLimit;
    private final List<String> violations = new ArrayList<>();

    public int getPaymentId() {
        return paymentId;
    }

    public int getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(int availableLimit) {
        this.availableLimit = availableLimit;
    }

    public List<String> getViolations() {
        return violations;
    }

    public void setViolations(String violation) {
        this.violations.add(violation);
    }

    public boolean hasViolations() {
        return this.violations.size() > 0;
    }

    @Override
    public String toString() {
        JsonObject response = new JsonObject();
        JsonElement jsonElement = Tools.gson.toJsonTree(this);
        response.add(PAYMENT_SESSION, jsonElement);

        return response.toString();
    }

    public void validatePaymentRules(PaymentRules paymentRules) {
        if (paymentRules == null) {
            this.violations.add(PAYMENT_RULES_NOT_INITIALIZED);
        } else {
            this.availableLimit = paymentRules.getMaxLimit();
        }
    }

    public void validateUniqueSession(Map<Integer, PaymentSession> paymentSessionMap) {
        if (paymentSessionMap.containsKey(this.paymentId)) {
            this.violations.add(PAYMENT_SESSION_ALREADY_INITIALIZED);
        }
    }

    public void storeSession(Map<Integer, PaymentSession> paymentSessions) {
        if (!hasViolations()){
            paymentSessions.put(this.paymentId, this);
        }
    }

    public void sessionValidations(PaymentRules paymentRules, Map<Integer, PaymentSession> paymentSessions) {
        validatePaymentRules(paymentRules);
        validateUniqueSession(paymentSessions);
        storeSession(paymentSessions);
    }
}
