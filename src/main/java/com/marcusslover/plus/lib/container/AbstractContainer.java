package com.marcusslover.plus.lib.container;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marcusslover.plus.lib.container.type.MapContainer;
import com.marcusslover.plus.lib.container.type.SingleContainer;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Abstract container that represents a parent of files, similar to a folder.
 * Default implementations are {@link MapContainer} and {@link SingleContainer}.
 *
 * @param <K> Value type.
 */
@Data
public abstract class AbstractContainer<K> {
    protected static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    protected File parentFolder;

    /**
     * Gets the Gson instance.
     * By default, it is {@link #DEFAULT_GSON}.
     *
     * @return Gson instance.
     */
    protected @NotNull Gson getGson() {
        return DEFAULT_GSON;
    }

    /**
     * Push the updated object to the cache and save it to the file.
     *
     * @param key Key of the object.
     */
    public abstract void update(@NotNull K key);

    /**
     * Attempts to load all the data from the container.
     * Look at {@link com.marcusslover.plus.lib.container.extra.InitialLoading} for more information.
     */
    public abstract void loadAllData();
}

