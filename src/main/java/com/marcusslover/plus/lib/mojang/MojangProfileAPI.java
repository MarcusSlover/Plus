package com.marcusslover.plus.lib.mojang;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.SneakyThrows;
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
     * Gets the cached profile of a player.
     * This function does not call the Mojang API. It only returns the cached profile.
     *
     * @param uuid The UUID of the player.
     * @return The cached profile of the player. If the player is not cached, then an empty optional is returned.
     */
    private static @NotNull Optional<MojangProfile> searchByUuid(@NotNull UUID uuid) {
        return CACHED_PROFILE_MAP.values().stream().filter(mojangProfile -> mojangProfile.getUuid().equals(uuid)).findFirst();
    }


    /**
     * Cleans the cache of a player.
     *
     * @param uuid The UUID of the player.
     */
    public static void clean(@NotNull UUID uuid) {
        CACHED_PROFILE_MAP.remove(uuid);
    }

    private static @NotNull CompletableFuture<@Nullable JsonObject> makeJsonRequest(URL url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create a connection to the Mojang API
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
                return jsonElement.getAsJsonObject();
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        });
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
        return cachedProfile.map(CompletableFuture::completedFuture).orElseGet(() -> fetchProfileByUsername(username));
    }

    /**
     * Gets the username of a player based on their UUID.
     * This function is asynchronous and will return a {@link CompletableFuture}
     * that will be completed when the UUID is retrieved.
     *
     * @param uuid The UUID of the player.
     * @return The username of the player.
     */
    public static @NotNull CompletableFuture<@Nullable MojangProfile> getUsername(@NotNull UUID uuid) {
        Optional<MojangProfile> cachedProfile = searchByUuid(uuid);
        return cachedProfile.map(CompletableFuture::completedFuture).orElseGet(() -> fetchProfileByUuid(uuid));
    }

    /**
     * Fetches the profile of a player from the player's UUID.
     * This method bypasses the cache, but it does still update it.
     * @param uuid The UUID to fetch the profile for.
     * @return The newly-fetched profile.
     */
    @SneakyThrows
    public static @NotNull CompletableFuture<@Nullable MojangProfile> fetchProfileByUuid(@NotNull UUID uuid) {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidToString(uuid));
        return makeJsonRequest(url).thenApply((json) -> {
            if (json == null) return null;
            try {
                String name = json.get("name").getAsString();
                MojangProfile mojangProfile = new MojangProfile(uuid, name, System.currentTimeMillis());
                CACHED_PROFILE_MAP.put(uuid, mojangProfile);
                return mojangProfile;
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Fetches the profile of a player from the player's username.
     * This method bypasses the cache, but it does still update it.
     * @param username The username to fetch the profile for.
     * @return The newly-fetched profile.
     */
    @SneakyThrows
    public static @NotNull CompletableFuture<@Nullable MojangProfile> fetchProfileByUsername(@NotNull String username) {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
        return makeJsonRequest(url).thenApply((json) -> {
            if (json == null) return null;
            try {
                if (!json.has("id")) {
                    throw new RuntimeException("Invalid response from Mojang API");
                }
                String id = json.get("id").getAsString();
                UUID uuid;

                // Convert the UUID to a UUID object
                try {
                    uuid = parseUuid(id);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Could not parse UUID from Mojang API response", e);
                }

                // Create the profile
                MojangProfile mojangProfile = new MojangProfile(uuid, username, System.currentTimeMillis());

                // Cache the profile
                CACHED_PROFILE_MAP.put(uuid, mojangProfile);

                // Return the profile
                return mojangProfile;
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        });
    }


    /**
     * Translate a UUID from the style used in the Mojang API to the one used by Java.
     * @param uuid The UUID to translate. This should be in a dashless form.
     * @return The UUID.
     * @see UUID#fromString(String)
     * @see #uuidToString(UUID)
     */
    public static UUID parseUuid(String uuid) {
        var translated = uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
        return UUID.fromString(translated);
    }

    /**
     * Translate a UUID from the style used in Java to the one used in the Mojang API.
     * @param uuid The UUID to translate.
     * @return The dashless UUID.
     * @see #parseUuid(String)
     */
    public static String uuidToString(UUID uuid) {
        return uuid.toString().replace("-", "");
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
            return getUniqueId(this.name).thenApply(mojangProfile -> mojangProfile);
        }

        /**
         * Converts the Mojang profile to a Paper profile.
         *
         * @return The Paper profile.
         */
        public @NotNull PlayerProfile asPaperProfile() {
            return Bukkit.createProfile(this.uuid, this.name);
        }

        /**
         * Converts the Mojang profile to a Bukkit profile. (old API)
         *
         * @return The Bukkit profile.
         * @deprecated Use {@link #asPaperProfile()} instead.
         */
        @Deprecated
        public @NotNull org.bukkit.profile.PlayerProfile asBukkitProfile() {
            return Bukkit.createPlayerProfile(this.uuid, this.name);
        }
    }

}
