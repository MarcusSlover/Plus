package me.marcusslover.plus;

import me.marcusslover.plus.lib.Plus;
import me.marcusslover.plus.test.WorldConfig;
import me.marcusslover.plus.test.WorldJson;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlusPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        /*Test*/
        WorldConfig config = new WorldConfig(Plus.file("world.yml"));
        WorldJson json = new WorldJson(Plus.file("world.json"));


    }
}
