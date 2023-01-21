package com.marcusslover.plus.lib.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marcusslover.plus.lib.world.WorldPoint;

public class JsonUtil {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(WorldPoint.class, WorldPoint.ADAPTER)
            .create();

    private JsonUtil() {
    }
}
