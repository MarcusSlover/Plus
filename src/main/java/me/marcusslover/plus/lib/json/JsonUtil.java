package me.marcusslover.plus.lib.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class JsonUtil {
    public static final Gson GSON = new GsonBuilder().create();
    public static final JsonParser PARSER = new JsonParser();
}
