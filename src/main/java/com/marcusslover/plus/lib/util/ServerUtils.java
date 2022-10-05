package com.marcusslover.plus.lib.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ServerUtils {
    /**
     * Returns every player that has joined this server in the past.
     *
     * @return List of OfflinePlayer objects that have connected to the server.
     */
    public static List<OfflinePlayer> getOfflinePlayerList() {
        return Arrays.asList(Bukkit.getServer().getOfflinePlayers());
    }

    /**
     * Returns every player's UUID that has joined this server in the past.
     *
     * @return List of UUID objects originating from the OfflinePlayer objects that have connected to the server.
     */
    public static List<UUID> getOfflinePlayerIdList() {
        return getOfflinePlayerList().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
    }

    /**
     * Returns every player's name that has joined this server in the past.
     *
     * @return List of String objects originating from the OfflinePlayer objects that have connected to the server.
     */
    public static List<String> getOfflinePlayerNameList() {
        return getOfflinePlayerList().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
    }

    /**
     * Returns every player that is currently connected to the server.
     *
     * @return List of Player objects that are currently connected to the server.
     */
    public static List<? extends Player> getOnlinePlayerList() {
        return new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
    }

    /**
     * Returns every player's UUID that is currently connected to the server.
     *
     * @return List of UUID objects originating from the Player objects that are currently connected to the server.
     */
    public static List<UUID> getOnlinePlayerIdList() {
        return getOnlinePlayerList().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
    }

    /**
     * Returns every player's name that is currently connected to the server.
     *
     * @return List of String objects originating from the Player objects that are currently connected to the server.
     */
    public static List<String> getOnlinePlayerNameList() {
        return getOnlinePlayerList().stream().map(Player::getName).collect(Collectors.toList());
    }

    /**
     * Gets the plugin that called the calling method of this method
     *
     * @return The plugin which called the method
     */
    public static Plugin getCallingPlugin() {
        Exception ex = new Exception();
        try {
            Class<?> clazz = Class.forName(ex.getStackTrace()[2].getClassName());
            Plugin plugin = JavaPlugin.getProvidingPlugin(clazz);
            return plugin.isEnabled() ? plugin : Bukkit.getPluginManager().getPlugin(plugin.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return The server version String (ex: 1.16.4)
     */
    public static String getServerVersion() {
        String version = Bukkit.getVersion();
        String[] split = version.split(" ");
        return split[split.length - 1].trim().replace(")", "");
    }

    /**
     * Gets all non-abstract, non-interface classes which extend a certain class within a plugin
     *
     * @param plugin The plugin
     * @param clazz  The class
     * @param <T>    The type of the class
     * @return The list of matching classes
     */
    public static <T> List<Class<? extends T>> getExtendingClasses(Plugin plugin, Class<T> clazz) {
        List<Class<? extends T>> list = new ArrayList<>();
        try {
            ClassLoader loader = plugin.getClass().getClassLoader();
            JarFile file = new JarFile(new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }
                name = name.substring(0, name.length() - 6).replace("/", ".");
                Class<?> c;
                try {
                    c = Class.forName(name, true, loader);
                } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                    continue;
                }
                if (!clazz.isAssignableFrom(c) || Modifier.isAbstract(c.getModifiers()) || c.isInterface()) {
                    continue;
                }
                list.add((Class<? extends T>) c);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return list;
    }
}