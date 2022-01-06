package com.example.blind_test.shared;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class CommunicationTypes {
    public static Type mapJsonTypeData = new TypeToken<Map<String, String>>() {
    }.getType();
}
