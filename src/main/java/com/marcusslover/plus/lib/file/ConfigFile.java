package com.marcusslover.plus.lib.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ConfigFile extends AbstractFile {
    protected YamlConfiguration yamlConfiguration;

    public ConfigFile(@NotNull File file) {
        super(file);
    }

    @Override
    public boolean isSet(@NotNull String key) {
        if (this.yamlConfiguration == null) {
            return false;
        }
        return this.yamlConfiguration.isSet(key);
    }

    @Override
    public void load() {
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public void save() {
        try {
            if (this.yamlConfiguration != null) {
                this.yamlConfiguration.save(this.file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public YamlConfiguration getYamlConfiguration() {
        return this.yamlConfiguration;
    }

    public void setYamlConfiguration(@NotNull YamlConfiguration yamlConfiguration) {
        this.yamlConfiguration = yamlConfiguration;
    }
}
