package com.marcusslover.plus.lib.common;

import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IMetaContainer<C, V extends PersistentDataHolder> {
    @Nullable V meta();

    @NotNull C meta(@NotNull V meta);

    @NotNull C meta(@NotNull Consumer<@NotNull V> meta);
}
