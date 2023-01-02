package com.jpforero.challenge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;

public class Tools {
    public static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(LOWER_CASE_WITH_DASHES)
            .create();
}
