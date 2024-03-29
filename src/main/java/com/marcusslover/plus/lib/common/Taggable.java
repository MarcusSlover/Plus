package com.marcusslover.plus.lib.common;

import com.marcusslover.plus.lib.item.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("DataFlowIssue")
public abstract class Taggable<V extends IMetaContainer<V, P>, P extends PersistentDataHolder> implements IMetaContainer<Item, ItemMeta> {
    protected abstract @NotNull V holder();

    // Alias for holder()
    public @NotNull V tagHolder() {
        return this.holder();
    }

    /**
     * Check if the item has a tag with the given key.
     *
     * @param key  The key of the tag.
     * @param type The type of the tag.
     * @return True if the item has the tag, false otherwise.
     */
    public boolean hasTag(@NotNull String key, @NotNull PersistentDataType<?, ?> type) {
        if (this.holder().meta() == null) {
            return false;
        }

        PersistentDataContainer p = this.holder().meta().getPersistentDataContainer();
        NamespacedKey n = new NamespacedKey("plus", key);

        return p.has(n, type);
    }

    // Alias for setTag()
    public @NotNull <K, Z> V tag(@NotNull String key, @NotNull Z value, @NotNull PersistentDataType<K, Z> type) {
        return this.setTag(key, value, type);
    }

    /**
     * Set a tag to the item.
     *
     * @param key   The key of the tag.
     * @param value The value of the tag.
     * @param type  The type of the value. You can use {@link DataType} for all the default types.
     * @param <K>   The type of the value.
     * @return The holder.
     */
    public @NotNull <K, Z> V setTag(@NotNull String key, @NotNull Z value, @NotNull PersistentDataType<K, Z> type) {
        this.holder().meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, type, value);
        });

        return this.holder();
    }

    // Alias for getTag()
    public @NotNull <K, Z> Optional<Z> tag(@NotNull String key, @NotNull PersistentDataType<K, Z> type) {
        return this.getTag(key, type);
    }

    /**
     * Remove the tag with the given key.
     *
     * @param key The key of the tag.
     * @return The holder.
     */
    public @NotNull V removeTag(@NotNull String key) {
        this.holder().meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.remove(n);
        });

        return this.holder();
    }

    /**
     * Get the value of the tag with the given key.
     *
     * @param key  The key of the tag.
     * @param type The type of the value. You can use {@link DataType} for all the default types.
     * @param <K>  The type of the value.
     * @return The optional value of the tag. If the item doesn't have the tag, the optional will be empty.
     */
    public @NotNull <K, Z> Optional<Z> getTag(@NotNull String key, @NotNull PersistentDataType<K, Z> type) {
        if (this.holder().meta() == null) {
            return Optional.empty();
        }

        PersistentDataContainer p = this.holder().meta().getPersistentDataContainer();
        NamespacedKey n = new NamespacedKey("plus", key);

        if (p.has(n, type)) {
            return Optional.ofNullable(p.get(n, type));
        }

        return Optional.empty();
    }
}
