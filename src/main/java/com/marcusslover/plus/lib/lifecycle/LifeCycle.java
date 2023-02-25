package com.marcusslover.plus.lib.lifecycle;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Represents a wrapper for a life cycle object.
 *
 * @param <T> the type of the life cycle
 */
public class LifeCycle<T extends ILifeCycle<?>> {
    private final @NotNull Supplier<T> instance; // the wrapped value

    /**
     * Creates a new life cycle.
     *
     * @param instance the instance
     */
    public LifeCycle(@NotNull Supplier<T> instance) {
        this.instance = instance;
    }

    /**
     * Unregisters the life cycle.
     */
    public void unregister() {
        try {
            this.instance.get().unregister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
