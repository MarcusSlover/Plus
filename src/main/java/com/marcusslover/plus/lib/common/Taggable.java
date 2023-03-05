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

    public boolean hasTag(@NotNull String key, @NotNull PersistentDataType<?, ?> type) {
        if (this.holder().meta() == null) {
            return false;
        }

        PersistentDataContainer p = this.holder().meta().getPersistentDataContainer();
        NamespacedKey n = new NamespacedKey("plus", key);

        return p.has(n, type);
    }

    public @NotNull <K> V setTag(@NotNull String key, @NotNull K value, @NotNull PersistentDataType<K, K> type) {
        this.holder().meta(itemMeta -> {
            PersistentDataContainer p = itemMeta.getPersistentDataContainer();
            NamespacedKey n = new NamespacedKey("plus", key);
            p.set(n, type, value);
        });

        return this.holder();
    }

    public @NotNull <K> Optional<K> getTag(@NotNull String key, @NotNull PersistentDataType<K, K> type) {
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
