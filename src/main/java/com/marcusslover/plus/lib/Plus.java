package com.marcusslover.plus.lib;

import com.marcusslover.plus.PlusPlugin;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class Plus {
    private static Plus instance;

    private Plus() {
        instance = this;
    }

    @NotNull
    public static Plus get() {
        return instance == null ? new Plus() : instance;
    }

    @NotNull
    public static File file(@NotNull String path) {
        Plus plus = get();
        Plugin plugin = plus.plugin();
        return new File(plugin.getDataFolder(), path);
    }

    @NotNull
    private Plugin plugin() {
        return PlusPlugin.getPlugin(PlusPlugin.class);
    }
}