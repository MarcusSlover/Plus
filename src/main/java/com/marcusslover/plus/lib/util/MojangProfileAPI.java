package com.marcusslover.plus.lib.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Allows for the retrieval of a player's UUID from their username.
 *
 * @author MarcusSlover
 */
public class MojangProfileAPI {
    /*Cached profiles, does not get reset until the event is over.*/
    private static final Map<UUID, MojangProfile> CACHED_PROFILE_MAP = new HashMap<>();

    private MojangProfileAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Gets the cached profile of a player.
     * This function does not call the Mojang API. It only returns the cached profile.
     *
     * @param username The username of the player.
     * @return The cached profile of the player. If the player is not cached, then an empty optional is returned.
     */
    private static @NotNull Optional<MojangProfile> searchByUsername(@NotNull String username) {
        return CACHED_PROFILE_MAP.values().stream().filter(mojangProfile -> mojangProfile.getName().equals(username)).findFirst();
    }

    /**
     * Cleans the cache of a player.
     *
     * @param uuid The UUID of the player.
     */
    public static void clean(@NotNull UUID uuid) {
        CACHED_PROFILE_MAP.remove(uuid);
    }

    /**
     * Gets the UUID of a player from their username.
     * This function is asynchronous and will return a {@link CompletableFuture}
     * that will be completed when the UUID is retrieved.
     *
     * @param username The username of the player.
     * @return The UUID of the player.
     */
    public static @NotNull CompletableFuture<@Nullable MojangProfile> getUniqueId(@NotNull String username) {
        // Check if the username is in the cache
        Optional<MojangProfile> cachedProfile = searchByUsername(username);
        return cachedProfile.map(CompletableFuture::completedFuture).orElseGet(() -> CompletableFuture.supplyAsync(() -> {
            try {
                // Create a connection to the Mojang API
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + System.currentTimeMillis());
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setRequestMethod("GET");

                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                httpURLConnection.disconnect();

                // Parse the response
                JsonElement jsonElement = JsonParser.parseString(content.toString());
                if (!jsonElement.isJsonObject()) {
                    throw new RuntimeException("Invalid response from Mojang API");
                }

                JsonObject json = jsonElement.getAsJsonObject();
                if (!json.has("id")) {
                    throw new RuntimeException("Invalid response from Mojang API");
                }
                String id = json.get("id").getAsString();
                UUID uuid;

                // Convert the UUID to a UUID object
                try {
                    uuid = UUID.fromString(id.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Could not parse UUID from Mojang API response", e);
                }

                // Create the profile
                MojangProfile mojangProfile = new MojangProfile(uuid, username, System.currentTimeMillis());

                // Cache the profile
                CACHED_PROFILE_MAP.put(uuid, mojangProfile);

                // Return the profile
                return mojangProfile;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }));

    }

    /**
     * Gets the cached map of profiles.
     *
     * @return The cached map of profiles.
     */
    public static Map<UUID, MojangProfile> getCachedProfileMap() {
        return CACHED_PROFILE_MAP;
    }

    /**
     * An object that keeps information about a player's name and UUID.
     * Cached profiles are used to prevent unnecessary requests to the Mojang API.
     */
    @Data
    public static class MojangProfile {
        /*Immutable data*/
        private final @NotNull UUID uuid;
        private final @NotNull String name;
        private final long timestamp; // Just in case we need to know when the profile was cached.

        /**
         * Attempts to update the whole Mojang profile.
         * This function is asynchronous and will return a {@link CompletableFuture}
         * that will be completed when the profile is updated.
         * All the results are cached.
         *
         * @return The updated Mojang profile.
         */
        public @NotNull CompletableFuture<@Nullable MojangProfile> update() {
            return getUniqueId(name).thenApply(mojangProfile -> mojangProfile);
        }

        /**
         * Converts the Mojang profile to a Paper profile.
         *
         * @return The Paper profile.
         */
        public @NotNull PlayerProfile asPaperProfile() {
            return Bukkit.createProfile(uuid, name);
        }

        /**
         * Converts the Mojang profile to a Bukkit profile. (old API)
         *
         * @return The Bukkit profile.
         * @deprecated Use {@link #asPaperProfile()} instead.
         */
        @Deprecated
        public @NotNull org.bukkit.profile.PlayerProfile asBukkitProfile() {
            return Bukkit.createPlayerProfile(uuid, name);
        }
    }

}
