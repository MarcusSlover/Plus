package me.marcusslover.plus.lib.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFile implements IFile {
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

    @NotNull
    public File getFile() {
        return file;
    }
}
