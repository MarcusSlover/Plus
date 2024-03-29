package com.marcusslover.plus.lib.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFile implements IFile {
    @NotNull
    protected final File file;

    public AbstractFile(@NotNull File file) {
        if (!file.exists()) {
            try {
                boolean fileNewFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.file = file;
    }

    public abstract boolean isSet(@NotNull String key);

    public @NotNull File getFile() {
        return this.file;
    }
}
