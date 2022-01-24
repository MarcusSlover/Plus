package me.marcusslover.plus.lib.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class ConfigFile extends AbstractFile {
    protected YamlConfiguration yamlConfiguration;

    public ConfigFile(@NotNull File file) {
        super(file);
    }

    @Override
    public boolean isSet(@NotNull String key) {
        if (yamlConfiguration == null) return false;
        return yamlConfiguration.isSet(key);
    }

    @Override
    public void load() {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() {
        try {
            if (yamlConfiguration != null) yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }

    public void setYamlConfiguration(@NotNull YamlConfiguration yamlConfiguration) {
        this.yamlConfiguration = yamlConfiguration;
    }
}
