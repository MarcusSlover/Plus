package com.marcusslover.plus.lib.mojang;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerProfile {
    @NotNull
    public static final PlayerProfile EMPTY = new PlayerProfile("", null);
    private final String name;
    private final UUID uniqueId;

    public PlayerProfile(String name, UUID uuid) {
        this.name = name;
        this.uniqueId = uuid;
    }

    public static PlayerProfile of(String name, UUID uuid) {
        return new PlayerProfile(name, uuid);
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
}