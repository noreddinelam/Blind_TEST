package com.example.blind_test.shared.gson_configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class GsonConfiguration {
    public final static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
            new shared.gson_configuration.LocalDateTimeSerializer()).registerTypeAdapter(LocalDateTime.class,new shared.gson_configuration.LocalDateTimeDeserializer()).setPrettyPrinting().create();
}
