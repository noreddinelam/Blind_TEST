package com.example.blind_test.shared;

import com.example.blind_test.front.models.Game;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class CommunicationTypes {
    public static Type mapJsonTypeData = new TypeToken<Map<String, String>>() {
    }.getType();
    public static Type mapListGameJsonTypeData = new TypeToken<Map<String, List<Game>>>() {
    }.getType();
}
