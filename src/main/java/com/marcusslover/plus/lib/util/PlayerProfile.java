package com.marcusslover.plus.lib.util;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerProfile {
    private final String name;
    private final UUID uniqueId;

    @NotNull
    public static final PlayerProfile EMPTY = new PlayerProfile("", null);

    public PlayerProfile(String name, UUID uuid) {
        this.name = name;
        this.uniqueId = uuid;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "PlayerProfile{" +
               "name='" + this.name + '\'' +
               ", uniqueId=" + this.uniqueId +
               '}';
    }

    public static PlayerProfile of(String name, UUID uuid) {
        return new PlayerProfile(name, uuid);
    }
}