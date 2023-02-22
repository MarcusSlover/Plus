package com.marcusslover.plus.lib.file;

import com.marcusslover.plus.lib.server.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileUtils {
    /**
     * Returns the Config located at the "path" with extension "ext"
     * <br /><br />
     * Note: If the file does not exist it will try to be saved from the corresponding location in the plugins resources
     *
     * @param path Path to file, using "." as a separator for folders (ex: {@literal "data.players"})
     * @param ext  File extension (ex: {@literal "yml"})
     * @return org.bukkit.configuration.file.FileConfiguration
     */
    public static FileConfiguration getConfig(String path, String ext, @NotNull JavaPlugin instance) {
        return YamlConfiguration.loadConfiguration(getFile(path, ext, instance));
    }

    /**
     * Returns the Config located at the "path" with extension "ext"
     * <br /><br />
     * Note: If the file does not exist it will try to be saved from the corresponding location in the plugins resources
     *
     * @param path Path to file, using "." as a separator for folders (ex: {@literal "data.players"})
     * @param ext  File extension (ex: {@literal "yml"})
     * @return org.bukkit.configuration.file.FileConfiguration
     */
    public static FileConfiguration getConfig(String path, String ext) {
        JavaPlugin instance = (JavaPlugin) ServerUtils.getCallingPlugin();
        if (instance == null) {
            throw new NullPointerException("Obtained plugin instance was null.");
        }
        return YamlConfiguration.loadConfiguration(getFile(path, ext, instance));
    }

    /**
     * Returns the File located at the "path" with extension "ext".
     * <br /><br />
     * Note: If the file does not exist it will try to be saved from the corresponding location in the plugins resources
     *
     * @param path Path to file, using "." as a separator for folders (ex: {@literal "data.players"})
     * @param ext  File extension (ex: {@literal "yml"})
     * @return java.io.File
     */
    public static File getFile(String path, String ext) {
        JavaPlugin instance = (JavaPlugin) ServerUtils.getCallingPlugin();
        if (instance == null) {
            throw new NullPointerException("Obtained plugin instance was null.");
        }
        return getFile(path, ext, instance);
    }

    /**
     * Returns the File located at the "path" with extension "ext".
     * <br /><br />
     * Note: If the file does not exist it will try to be saved from the corresponding location in the plugins resources
     *
     * @param path Path to file, using "." as a separator for folders (ex: {@literal "data.players"})
     * @param ext  File extension (ex: {@literal "yml"})
     * @return java.io.File
     */
    public static File getFile(String path, String ext, @NotNull JavaPlugin instance) {
        String[] splitPath = path.split("\\.");
        StringBuilder parentPath = new StringBuilder().append(instance.getDataFolder().getAbsolutePath());
        String fileName = splitPath[splitPath.length - 1].concat(".").concat(ext);

        for (int i = 0; i < splitPath.length; ++i) {
            if (i != splitPath.length - 1) {
                parentPath.append(File.separator);
                parentPath.append(splitPath[i]);
            }
        }

        File file = new File(parentPath.toString(), fileName);

        if (!file.exists()) {
            try {
                instance.saveResource(path.replace(".", File.separator).concat("." + ext), true);
            } catch (Exception ignored) {
                try {
//                  noinspection ResultOfMethodCallIgnored
                    file.getParentFile().mkdirs();
//                  noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                } catch (Exception e) {
                    Bukkit.getLogger().warning("There was an issue creating " + fileName);
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static File getFolder(@NotNull String path) {
        JavaPlugin instance = (JavaPlugin) ServerUtils.getCallingPlugin();

        if (instance == null) {
            throw new NullPointerException("Obtained plugin instance was null.");
        }

        if (path.isBlank()) {
            return null;
        }

        String fixed = String.join(File.separator, path.split("\\."));

        String fullPath = instance.getDataFolder().getAbsolutePath() + File.separator + fixed;

        File file = new File(fullPath);

        boolean useless = file.mkdirs();

        return file;
    }
}