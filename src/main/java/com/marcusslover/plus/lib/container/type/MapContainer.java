package com.marcusslover.plus.lib.container.type;

import com.google.gson.Gson;
import com.marcusslover.plus.lib.container.AbstractContainer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Container that represents a map of objects.
 * Key is the name of the file. Files are dynamic.
 * Value is the object. Each value is serialized to its own json file.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class MapContainer<K, V> extends AbstractContainer<K> {
    /*Default string-to-string solution for file naming*/
    protected static final Function<String, String> TRANSFORMER = (x -> x);
    protected static final Function<String, String> COMPOSER = (x -> x);
    /*Container data*/
    protected final Map<K, V> cache = new HashMap<>();
    /*File naming solution*/
    private final Function<K, String> keyTransformer;
    private final Function<String, K> keyComposer;
    private final Class<V> valueType;

    /**
     * Creates a new instance of an object.
     * <p
     * This function is called when the object with the given key
     * does not exist and has to be created for the first time.
     * </p>
     *
     * @param key The key of the object.
     * @return The object.
     */
    protected abstract @NotNull V emptyValue(@NotNull K key);

    /**
     * Called when a new object gets loaded.
     * <p>
     * This function is mainly called when {@link #storeLocally(K, V)} is called.
     * </p>
     *
     * @param value The object that was most recently loaded.
     */
    protected void onValueLoaded(@NotNull V value) {
        // do some extra thing when the value is loaded
    }

    /**
     * Called when an object gets unloaded.
     * <p>
     * This function is mainly called when {@link #cleanLocally(K)} is called.
     * </p>
     *
     * @param value The object that was most recently unloaded.
     */
    protected void onValueUnloaded(@NotNull V value) {
        // do some extra thing when the value is unloaded
    }

    @Override
    public void update(@NotNull K key) {
        this.writeData(key, this.retrieveLocally(key));
    }

    /**
     * Loads an object from the file.
     * The key is the name of the file.
     *
     * @param key Key to the object.
     * @return The object.
     */
    public @NotNull V loadData(@NotNull K key) {
        if (this.cache.containsKey(key)) {
            //noinspection DataFlowIssue
            return this.retrieveLocally(key);
        }
        V data = this.readData(key);
        this.storeLocally(key, data);
        return data;
    }

    /**
     * Loads all the objects from the files.
     * Called during start of the plugin, only when the container
     * is annotated with {@link com.marcusslover.plus.lib.container.extra.InitialLoading}.
     */
    @Override
    public void loadAllData() {
        File[] files = this.parentFolder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            K apply = this.keyComposer.apply(fileName.replaceFirst("(\\.json)", ""));
            V read = this.read(fileName);
            if (this.containsKeyLocally(apply)) {
                continue;
            }
            this.storeLocally(apply, read);
        }
    }

    /**
     * Unloads an object from the cache and saves it to the file.
     *
     * @param key Key to the object.
     */
    public void saveData(@NotNull K key) {
        if (!this.cache.containsKey(key)) {
            return;
        }
        V data = this.retrieveLocally(key);
        this.writeData(key, data);
        this.cleanLocally(key);
    }

    /**
     * Checks if object with the given key is loaded in the cache.
     *
     * @param key Key to the object.
     * @return True if the object is loaded, false otherwise.
     */
    public boolean containsKeyLocally(@NotNull K key) {
        return this.cache.containsKey(key);
    }

    /**
     * Checks if the object is loaded in the cache.
     * <p>
     * The check does not include any files, only the cache!
     * </p>
     *
     * @param value Object to check.
     * @return True if the object is loaded, false otherwise.
     */
    public boolean containsValueLocally(@NotNull V value) {
        return this.cache.containsValue(value);
    }

    /**
     * Cleans the object from the cache.
     * <p>
     * This function does not write the object to the file.
     * </p>
     *
     * @param key Key to the object.
     */
    public void cleanLocally(@NotNull K key) {
        V value = this.retrieveLocally(key);
        if (value != null) {
            this.onValueUnloaded(value);
        }
        this.cache.remove(key);
    }

    /**
     * Puts the object in the cache.
     * <p>
     * This function only puts the object in the cache.
     * It doesn't save the object to the file -> Use {@link #update(K)} for that
     * or {@link #writeData(K, V)}.
     * </p>
     *
     * @param key   Key to the object.
     * @param value Object to put.
     */
    public void storeLocally(@NotNull K key, @Nullable V value) {
        if (value == null) {
            this.cleanLocally(key);
        } else {
            this.onValueLoaded(value);
            this.cache.put(key, value);
        }
    }

    /**
     * Retrieves the object from the cache.
     * <p>
     * This function will not attempt to load the object from the file,
     * if it is not loaded -> Use {@link #loadData(K)} instead.
     * </p>
     *
     * @param key Key to the object.
     * @return The object or null if it is not loaded.
     */
    public @Nullable V retrieveLocally(@NotNull K key) {
        return this.cache.getOrDefault(key, null);
    }

    /**
     * Reads the object from the file.
     * <p>
     * If the file doesn't exist, it will create a new one.
     * Default value created by {@link #emptyValue(Object)} will be used.
     * </p>
     *
     * @param key Key to the object.
     * @return The object.
     */
    public @NotNull V readData(@NotNull K key) {
        String fileName = this.keyTransformer.apply(key);
        V read = this.read(fileName + ".json");
        return Objects.requireNonNullElseGet(read, () -> this.emptyValue(key));
    }

    /**
     * Reads the object from the file.
     * <p>
     * This is an internal (raw) function -> Use {@link #readData(Object)} instead.
     * </p>
     *
     * @param fileName Name of the file.
     * @return The object.
     */
    public @Nullable V read(@NotNull String fileName) {
        V data;
        Gson gson = this.getGson();
        File file = new File(this.getParentFolder(), fileName);
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                data = gson.fromJson(fileReader, this.valueType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return data;
        }
        return null;
    }

    /**
     * Writes the object to the file.
     * <p>
     * This function does not unload anything from the cache.
     * The cache is not affected by this function.
     * If you want to delete the file, set the value to null!
     * </p>
     *
     * @param key   Key to the object.
     * @param value Object to write.
     */
    public void writeData(@NotNull K key, @Nullable V value) {
        String fileName = this.keyTransformer.apply(key);
        Gson gson = this.getGson();
        File file = new File(this.getParentFolder(), fileName + ".json");
        if (value == null) {
            file.delete();
            return;
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            gson.toJson(value, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all the loaded objects from the cache.
     *
     * @return All the loaded objects.
     */
    public @NotNull Collection<V> getValues() {
        return this.cache.values();
    }
}

