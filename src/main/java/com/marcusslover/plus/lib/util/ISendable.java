package com.marcusslover.plus.lib.util;

import org.jetbrains.annotations.NotNull;

public interface ISendable<T, V> {
    @NotNull V send(@NotNull T target);
}
