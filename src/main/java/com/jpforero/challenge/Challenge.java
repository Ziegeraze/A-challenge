package com.jpforero.challenge;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.util.*;

import static com.jpforero.challenge.Constants.*;

public class Challenge {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(new BufferedInputStream(System.in));
        String input;

        PaymentRules paymentRules = null;

        while (scanner.hasNext()) {
            input = scanner.nextLine();

            try {
                JsonObject convertedObject = Tools.gson.fromJson(input, JsonObject.class);

                if (convertedObject.has(PAYMENT_RULES)) {
                    paymentRules = Tools.gson.fromJson(convertedObject.get(PAYMENT_RULES), PaymentRules.class);
                    System.out.println(paymentRules);
                }
            } catch (JsonSyntaxException | NullPointerException e) {
                System.out.println(ERROR_INVALID_JSON);
            }
        }
    }
}
