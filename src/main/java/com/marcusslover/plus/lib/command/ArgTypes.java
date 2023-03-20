package com.marcusslover.plus.lib.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the argument types.
 */
public class ArgTypes {
    private final @NotNull List<Deserializer<?>> deserializers;

    ArgTypes() {
        this.deserializers = new LinkedList<>();
    }

    /**
     * Registers a deserializer.
     *
     * @param deserializer The deserializer.
     * @return The argument types.
     */
    public @NotNull ArgTypes register(@NotNull Deserializer<?> deserializer) {
        this.deserializers.add(deserializer);
        return this;
    }

    /**
     * Gets a deserializer.
     *
     * @param type The type of the deserializer.
     * @return The deserializer.
     */
    public @Nullable Deserializer<?> getDeserializer(@NotNull Class<?> type) {
        return this.deserializers.stream()
                .filter(deserializer -> deserializer.getClass().equals(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * Represents a deserializer.
     *
     * @param <T> The type of the deserializer.
     */
    public interface Deserializer<T> {
        /**
         * Deserializes the argument.
         *
         * @param arg The argument.
         * @return The deserialized argument.
         */
        @Nullable T deserialize(String arg) throws Exception;
    }
}
