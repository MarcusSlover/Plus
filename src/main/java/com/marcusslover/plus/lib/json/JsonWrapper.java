package com.marcusslover.plus.lib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class JsonWrapper {
    @NotNull
    private JsonElement jsonElement;

    public JsonWrapper(@NotNull JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    @NotNull
    public JsonElement getJsonElement() {
        return jsonElement;
    }

    @NotNull
    public JsonWrapper setJsonElement(@NotNull JsonElement jsonElement) {
        this.jsonElement = jsonElement;
        return this;
    }

    @NotNull
    public JsonWrapper forEach(@NotNull Consumer<JsonElement> consumer) {
        if (isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            array.forEach(consumer);
        }
        return this;
    }

    public boolean isJsonObject() {
        return jsonElement.isJsonObject();
    }

    public boolean isJsonPrimitive() {
        return jsonElement.isJsonPrimitive();
    }

    public boolean isJsonArray() {
        return jsonElement.isJsonArray();
    }

    private void jsonObject(Consumer<JsonObject> consumer) {
        if (isJsonObject()) consumer.accept(jsonElement.getAsJsonObject());
    }

    @NotNull
    public JsonWrapper remove(@NotNull String key) {
        jsonObject(json -> json.remove(key));
        return this;
    }

    public boolean hasKey(@NotNull String key) {
        AtomicBoolean atom = new AtomicBoolean(false);
        jsonObject(json -> atom.set(json.has(key)));
        return atom.get();
    }

    @NotNull
    public JsonWrapper set(@NotNull String key, @Nullable Object value) {
        JsonElement jsonElement = JsonUtil.GSON.toJsonTree(value);
        jsonObject(json -> json.add(key, jsonElement));
        return this;
    }

    @NotNull
    public <T> T get(@NotNull String key, @Nullable T defaultValue, @NotNull Class<T> type) {
        AtomicReference<T> atom = new AtomicReference<>(defaultValue);
        jsonObject(json -> {
            if (hasKey(key)) {
                JsonElement jsonElement = json.get(key);
                atom.set(JsonUtil.GSON.fromJson(jsonElement, type));
            } else atom.set(defaultValue);
        });
        return atom.get();
    }

    @NotNull
    public JsonWrapper set(@NotNull String key, @Nullable String value) {
        jsonObject(json -> json.addProperty(key, value));
        return this;
    }

    @NotNull
    public String get(@NotNull String key, @Nullable String defaultValue) {
        AtomicReference<String> atom = new AtomicReference<>(defaultValue);
        jsonObject(json -> {
            if (hasKey(key)) atom.set(json.get(key).getAsString());
            else atom.set(defaultValue);
        });
        return atom.get();
    }

    @NotNull
    public JsonWrapper set(@NotNull String key, @Nullable Number value) {
        jsonObject(json -> json.addProperty(key, value));
        return this;
    }

    @NotNull
    public Number get(@NotNull String key, @Nullable Number defaultValue) {
        AtomicReference<Number> atom = new AtomicReference<>(defaultValue);
        jsonObject(json -> {
            if (hasKey(key)) atom.set(json.get(key).getAsNumber());
            else atom.set(defaultValue);
        });
        return atom.get();
    }

    @NotNull
    public JsonWrapper set(@NotNull String key, @Nullable Character value) {
        jsonObject(json -> json.addProperty(key, value));
        return this;
    }

    @NotNull
    public Character get(@NotNull String key, @Nullable Character defaultValue) {
        AtomicReference<Character> atom = new AtomicReference<>(defaultValue);
        jsonObject(json -> {
            if (hasKey(key)) atom.set(json.get(key).getAsCharacter());
            else atom.set(defaultValue);
        });
        return atom.get();
    }

    @NotNull
    public JsonWrapper set(@NotNull String key, @Nullable Boolean value) {
        jsonObject(json -> json.addProperty(key, value));
        return this;
    }

    @NotNull
    public Boolean get(@NotNull String key, @Nullable Boolean defaultValue) {
        AtomicReference<Boolean> atom = new AtomicReference<>(defaultValue);
        jsonObject(json -> {
            if (hasKey(key)) atom.set(json.get(key).getAsBoolean());
            else atom.set(defaultValue);
        });
        return atom.get();
    }
}
