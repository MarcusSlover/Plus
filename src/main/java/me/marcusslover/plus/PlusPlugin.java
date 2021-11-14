package me.marcusslover.plus;

import me.marcusslover.plus.lib.Plus;
import me.marcusslover.plus.lib.json.JsonWrapper;
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
        WorldConfig worldConfig = new WorldConfig(Plus.file("world.yml"));
        WorldJson worldJson = new WorldJson(Plus.file("world.json"));
        JsonWrapper json = new JsonWrapper(worldJson.getJsonElement());



    }
}
