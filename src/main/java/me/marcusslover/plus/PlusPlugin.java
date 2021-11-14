package me.marcusslover.plus;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlusPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }
}
