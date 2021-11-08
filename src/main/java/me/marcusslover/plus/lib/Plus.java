package me.marcusslover.plus.lib;

import me.marcusslover.plus.PlusPlugin;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Plus {
    private static Plus instance;

    private Plus() {
        instance = this;
    }

    @NotNull
    private Plugin plugin() {
        return PlusPlugin.getPlugin(PlusPlugin.class);
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


}
