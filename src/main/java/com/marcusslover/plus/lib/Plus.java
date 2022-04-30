package com.marcusslover.plus.lib;

import com.marcusslover.plus.PlusPlugin;
import com.marcusslover.plus.lib.command.CommandManager;
import com.marcusslover.plus.lib.item.Menu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class Plus {
    public static Plugin hook;
    private static Plus instance;

    private Plus() {
        instance = this;
    }

    @NotNull
    public static Plus get() {
        return instance == null ? new Plus() : instance;
    }

    public static void hook(Plugin plugin) {
        Plus.hook = plugin;
        Menu.initialize(plugin);
    }

    public static void unhook() {
        Menu.initialize(null);
        CommandMap commandMap = Bukkit.getCommandMap();
        Map<String, Command> knownCommands = commandMap.getKnownCommands();
        CommandManager commandManager = CommandManager.get();
        Set<Command> commandSet = commandManager.getCommandSet();
        for (Command command : commandSet) {
            String label = command.getLabel();
            knownCommands.remove(label);
        }
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