package com.jpforero.challenge;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import static com.jpforero.challenge.Constants.*;

public class Challenge {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(new BufferedInputStream(System.in));
        String input;

        PaymentRules paymentRules = null;
        final Map<Integer, PaymentSession> paymentSessions = new HashMap<>();
        LinkedList<PaymentSessionAuthorization> paymentSessionsAuthorizations = new LinkedList<>();

        while (scanner.hasNext()) {
            input = scanner.nextLine();

            try {
                JsonObject convertedObject = Tools.gson.fromJson(input, JsonObject.class);

                if (convertedObject.has(PAYMENT_RULES)) {
                    paymentRules = Tools.gson.fromJson(convertedObject.get(PAYMENT_RULES), PaymentRules.class);
                    System.out.println(paymentRules);
                } else if (convertedObject.has(PAYMENT_SESSION)) {
                    if (!convertedObject.getAsJsonObject(PAYMENT_SESSION).has(TIME)) {
                        PaymentSession paymentSession
                                = Tools.gson.fromJson(convertedObject.get(PAYMENT_SESSION), PaymentSession.class);

                        paymentSession.sessionValidations(paymentRules, paymentSessions);

                        System.out.println(paymentSession);
                    } else {
                        PaymentSessionAuthorization paymentSessionAuthorization
                                = Tools.gson.fromJson(convertedObject.get(PAYMENT_SESSION), PaymentSessionAuthorization.class);

                        paymentSessionAuthorization.sessionAuthValidations(paymentRules, paymentSessions, paymentSessionsAuthorizations);

                        System.out.println(paymentSessionAuthorization);
                    }
                }
            } catch (JsonSyntaxException | NullPointerException e) {
                System.out.println(ERROR_INVALID_JSON);
            }
        }
    }
}
