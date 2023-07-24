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
     * @deprecated The method is deprecated because you can achieve the same result with {@link #tag(String, PersistentDataType)},
     * where you can check if the returned {@link Optional} is empty or not.
     */
    @Deprecated
    public boolean hasTag(@NotNull String key, @NotNull PersistentDataType<?, ?> type) {
        if (this.holder().meta() == null) {
            return false;
        }

        PersistentDataContainer p = this.holder().meta().getPersistentDataContainer();
        NamespacedKey n = new NamespacedKey("plus", key);

        return p.has(n, type);
    }

    /**
     * Alias for {@link #setTag(String, Object, PersistentDataType)}.
     *
     * @see #setTag(String, Object, PersistentDataType)
     */
    public @NotNull <T, Z> V tag(@NotNull String key, @NotNull Z value, @NotNull PersistentDataType<T, Z> type) {
        return this.setTag(key, value, type);
    }

    /**
     * Set a tag to the item.
     *
     * @param key   The key of the tag.
     * @param value The value of the tag.
     * @param type  The type of the value. You can use {@link DataType} for all the default types.
     * @param <T>   The type that is serialised to NBT.
     * @param <Z>   The type that's used in code.
     * @return The holder.
     */
    public @NotNull <T, Z> V setTag(@NotNull String key, @NotNull Z value, @NotNull PersistentDataType<T, Z> type) {
        this.holder().meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, type, value);
        });

        return this.holder();
    }

    // Alias for getTag()
    public @NotNull <K> Optional<K> tag(@NotNull String key, @NotNull PersistentDataType<K, K> type) {
        return this.getTag(key, type);
    }

    /**
     * Get the value of the tag with the given key.
     *
     * @param key  The key of the tag.
     * @param type The type of the value. You can use {@link DataType} for all the default types.
     * @param <T>   The type that is serialised to NBT.
     * @param <Z>   The type that's used in code.
     * @return The optional value of the tag. If the item doesn't have the tag, the optional will be empty.
     */
    public @NotNull <T, Z> Optional<Z> getTag(@NotNull String key, @NotNull PersistentDataType<T, Z> type) {
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
