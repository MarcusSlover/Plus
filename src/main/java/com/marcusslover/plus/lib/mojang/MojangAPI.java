package com.marcusslover.plus.lib.mojang;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MojangAPI {
    static Cache<UUID, PlayerProfile> cache = CacheBuilder.newBuilder()
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .build();
    static HashMap<String, UUID> nameMap = new HashMap<>();

    public static PlayerProfile getPlayerProfile(UUID uuid) {
        PlayerProfile profile = cacheFetch(uuid);

        if (profile == null) {
            try {
                String name = "";

                JsonArray _array = getJson(getUrl(uuid), JsonArray.class);
                long _lastChange = 0;

                for (JsonElement element : _array) {
                    JsonObject _object = element.getAsJsonObject();

                    long _changedToAt = _object.get("changedToAt").getAsLong();

                    if (_changedToAt > _lastChange) {
                        name = _object.get("name").getAsString();

                        _lastChange = _changedToAt;
                    }
                }

                if (!name.isBlank()) {
                    profile = new PlayerProfile(name, uuid);

                    cache.put(profile.uniqueId(), profile);

                    nameMap.put(profile.name(), profile.uniqueId());
                }
            } catch (Throwable ignored) {
                return PlayerProfile.EMPTY;
            }
        }

        return profile == null ? PlayerProfile.EMPTY : profile;
    }

    private static <T> T getJson(URL url, Class<T> classOfT) throws IOException {
        Gson gson = new Gson();
        // Create URL
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setReadTimeout(5000);
        con.setConnectTimeout(5000);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestProperty("User-Agent", "Mozilla/4.0");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        InputStream in = con.getInputStream();
        String json = parseInput(in);

        return gson.fromJson(json, classOfT);
    }

    @NotNull
    public static PlayerProfile getPlayerProfile(String username) {
        PlayerProfile profile = cacheFetch(username);

        if (profile == null) {
            try {
                UUID playerUUID = null;

                JsonObject _object = getJson(getUrl(username), JsonObject.class);
                String id = _object.get("id").getAsString();
                String name = _object.get("name").getAsString();

                if (id.length() == 32) {
                    id = id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32);
                }

                if (id.length() == 36) {
                    playerUUID = UUID.fromString(id);
                }

                if (!name.isBlank() && playerUUID != null) {
                    profile = new PlayerProfile(name, playerUUID);

                    cache.put(profile.uniqueId(), profile);

                    nameMap.put(profile.name(), profile.uniqueId());
                }
            } catch (Throwable ignored) {
                return PlayerProfile.EMPTY;
            }
        }

        return profile == null ? PlayerProfile.EMPTY : profile;
    }

    @Nullable
    private static PlayerProfile cacheFetch(UUID uuid) {
        return cache.getIfPresent(uuid);
    }

    @Nullable
    private static PlayerProfile cacheFetch(String username) {
        if (nameMap.get(username) == null) {
            return null;
        }

        return cacheFetch(nameMap.get(username));
    }

    @NotNull
    private static URL getUrl(String name) throws Throwable {
        return new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
    }

    @NotNull
    private static URL getUrl(UUID uuid) throws Throwable {
        return new URL("https://api.mojang.com/user/profiles/" + uuid.toString() + "/names");
    }

    @NotNull
    private static String parseInput(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = in.read(buffer);
        StringBuilder builder = new StringBuilder();
        while (bytesRead > 0) {
            builder.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            bytesRead = in.read(buffer);
        }
        return builder.toString();
    }
}