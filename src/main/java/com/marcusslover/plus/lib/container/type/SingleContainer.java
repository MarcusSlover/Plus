package com.marcusslover.plus.lib.container.type;

import com.google.gson.Gson;
import com.marcusslover.plus.lib.container.AbstractContainer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Container that represents a single object.
 * The object is serialized to a single json file.
 * The file name is final and can be changed in the constructor.
 *
 * @param <V> Value type.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class SingleContainer<V> extends AbstractContainer<V> {
    private final Class<V> valueType;
    private final String fileName;
    protected @Nullable V cache = null;

    /**
     * Creates a new instance of an object.
     * <p>
     * This function is called when the object does not exist and has to be created for the first time.
     * </p>
     *
     * @return The object.
     */
    protected abstract @NotNull V emptyValue();

    /**
     * Called when a new object gets loaded.
     * <p>
     * This function is mainly called when {@link #storeLocally(V)} is called.
     * </p>
     *
     * @param value The object that was most recently loaded.
     */
    protected void onValueLoaded(@Nullable V value) {
        // do some extra thing when the value is loaded
    }

    /**
     * Called when an object gets unloaded.
     * <p>
     * This function is mainly called when {@link #cleanLocally()}} is called.
     * </p>
     *
     * @param value The object that was most recently unloaded.
     */
    protected void onValueUnloaded(@Nullable V value) {
        // do some extra thing when the value is unloaded
    }

    @Override
    public void update(@NotNull V value) {
        this.writeData(this.retrieveLocally());
    }

    /**
     * Loads an object from the file.
     *
     * @return The object.
     */
    public @NotNull V loadData() {
        if (this.cache != null) {
            return this.cache;
        }
        V data = this.readData();
        this.storeLocally(data);
        return data;
    }

    @Override
    public void loadAllData() {
        V v = this.readData();
        this.storeLocally(v);
    }

    /**
     * Unloads the data from the cache and saves it to the file.
     */
    public void saveData() {
        if (this.cache == null) {
            return;
        }
        V data = this.retrieveLocally();
        this.writeData(data);
        this.cleanLocally();
    }


    /**
     * Cleans the local cache.
     * <p>
     * This function does not delete the file.
     * </p>
     */
    public void cleanLocally() {
        this.onValueUnloaded(this.cache);
        this.cache = null;
    }

    /**
     * Stores locally the data.
     * <p>
     * This function does not write the data to the file.
     * </p>
     *
     * @param value The value to store.
     */
    public void storeLocally(@Nullable V value) {
        this.cache = value;
        this.onValueLoaded(value);
    }

    /**
     * Retrieves the object from the cache.
     * <p>
     * This function will not attempt to load the object from the file,
     * if it is not loaded -> Use {@link #loadData()} instead.
     * </p>
     *
     * @return The object or null if it is not loaded.
     */
    public @Nullable V retrieveLocally() {
        return this.cache;
    }

    /**
     * Reads the object from the file.
     * <p>
     * If the file doesn't exist, it will create a new one.
     * Default value created by {@link #emptyValue()} will be used.
     * </p>
     *
     * @return The object.
     */
    public @NotNull V readData() {
        V data;
        Gson gson = this.getGson();
        File file = new File(this.getParentFolder(), this.fileName + ".json");
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                data = gson.fromJson(fileReader, this.valueType);
            } catch (IOException e) {
                Bukkit.getLogger().severe("Could not read file: " + file.getAbsolutePath());
                throw new RuntimeException(e);
            }
            return data;
        } else {
            return this.emptyValue();
        }
    }

    /**
     * Writes the data to the file.
     * <p>
     * This function does not unload anything from the cache.
     * The cache is not affected by this function.
     * If you want to delete the file, set the value to null!
     * </p>
     *
     * @param value Object to write.
     */
    public void writeData(@Nullable V value) {
        Gson gson = this.getGson();
        File file = new File(this.getParentFolder(), this.fileName + ".json");
        if (value == null) {
            boolean delete = file.delete();
            if (!delete) {
                throw new RuntimeException("Could not delete file: " + file.getAbsolutePath());
            }
            return;
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(value, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

