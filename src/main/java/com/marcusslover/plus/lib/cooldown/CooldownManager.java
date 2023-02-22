package com.marcusslover.plus.lib.cooldown;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lets you easily manage player & global cooldowns.
 *
 * @author MarcusSlover
 */
public class CooldownManager {
    private final static UUID SERVER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static CooldownManager instance;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {
        instance = this;
    }


    /**
     * Checks if the player has a cooldown.
     *
     * @param player   The player to check or null if global.
     * @param key      The cooldown key.
     * @param duration The duration of the cooldown in milliseconds.
     * @return true if no cooldown, false if cooldown.
     */
    public static boolean check(@Nullable Player player, @NotNull String key, long duration) {
        return instance().check0(player, key, duration);
    }

    /**
     * Sets a cooldown for the player or global.
     *
     * @param player   The player to set the cooldown for or null if global.
     * @param key      The cooldown key.
     * @param duration The duration of the cooldown in milliseconds.
     */
    public static void set(@Nullable Player player, @NotNull String key, long duration) {
        instance().check0(player, key, duration);
    }

    /**
     * Removes a cooldown for the player or global.
     *
     * @param player The player to remove the cooldown for or null if global.
     */
    public static void remove(@Nullable Player player) {
        if (player == null) {
            instance().cooldowns.remove(SERVER_UUID);
        } else {
            instance().cooldowns.remove(player.getUniqueId());
        }
    }

    public static CooldownManager instance() {
        return instance == null ? new CooldownManager() : instance;
    }

    /**
     * Checks if the player has a cooldown.
     *
     * @param player   The player to check or null if global.
     * @param key      The cooldown key.
     * @param duration The duration of the cooldown in milliseconds.
     * @return true if no cooldown, false if cooldown.
     */
    private boolean check0(@Nullable Player player, @NotNull String key, long duration) {
        UUID uuid;
        if (player == null) {
            uuid = SERVER_UUID;
        } else {
            uuid = player.getUniqueId();
        }

        Map<String, Long> cooldown = this.cooldowns.getOrDefault(uuid, new HashMap<>());
        long now = System.currentTimeMillis();
        long then = cooldown.getOrDefault(key, 0L);
        long diff = now - then;
        boolean result = diff >= duration;
        if (result) {
            cooldown.put(key, now);
        }
        this.cooldowns.put(uuid, cooldown);

        return result;
    }
}
