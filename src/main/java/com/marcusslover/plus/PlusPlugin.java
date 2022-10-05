package com.marcusslover.plus;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class PlusPlugin extends JavaPlugin {

    private static PlusPlugin instance;


    @NotNull
    public static PlusPlugin get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        File dataFolder = this.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

    }
}
