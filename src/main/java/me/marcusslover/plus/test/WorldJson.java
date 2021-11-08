package me.marcusslover.plus.test;

import me.marcusslover.plus.lib.file.JsonFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class WorldJson extends JsonFile {
    public WorldJson(@NotNull File file) {
        super(file);
    }
}
