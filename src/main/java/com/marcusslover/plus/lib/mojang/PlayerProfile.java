package com.marcusslover.plus.lib.mojang;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PlayerProfile(String name, UUID uniqueId) {
    @NotNull
    public static final PlayerProfile EMPTY = new PlayerProfile("", null);

    public static PlayerProfile of(String name, UUID uuid) {
        return new PlayerProfile(name, uuid);
    }

    @Override
    public String toString() {
        return "PlayerProfile{" +
            "name='" + this.name + '\'' +
            ", uniqueId=" + this.uniqueId +
            '}';
    }
}