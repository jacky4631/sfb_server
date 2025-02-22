package com.mailvor.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.Reader;

/**
 * @author niefy
 */
public class GsonParser {
    private static final JsonParser JSON_PARSER = new JsonParser();

    public static JsonObject parse(String json) {
        return JSON_PARSER.parse(json).getAsJsonObject();
    }

    public static JsonObject parse(Reader json) {
        return JSON_PARSER.parse(json).getAsJsonObject();
    }

    public static JsonObject parse(JsonReader json) {
        return JSON_PARSER.parse(json).getAsJsonObject();
    }
}
