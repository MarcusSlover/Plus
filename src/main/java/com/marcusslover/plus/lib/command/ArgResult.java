package com.marcusslover.plus.lib.command;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A single argument result.
 */
@Data
@Accessors(fluent = true)
public class ArgResult {
    /**
     * The name of the argument.
     */
    private final @NotNull String name;

    /**
     * The parent context.
     */
    private final @NotNull ArgContext context;

    /**
     * Parsed argument value as an object.
     */
    private final @Nullable Object value;


    /**
     * Indicates if the argument value is present.
     *
     * @return True if the argument value is present.
     */
    public boolean isPresent() {
        return this.value != null;
    }

    /**
     * Indicates if the argument value is empty.
     *
     * @return True if the argument value is empty.
     */
    public boolean isEmpty() {
        return this.value == null;
    }

    /**
     * Returns the argument value if present, otherwise returns the default value.
     *
     * @param def The default value.
     * @return The argument value if present, otherwise returns the default value.
     */
    public @NotNull Object orElse(@NotNull Object def) {
        if (this.value == null) {
            return def;
        }
        return this.value;
    }

    /**
     * Returns the argument value, additionally casting it to the specified type.
     *
     * @param type The type.
     * @param <V>  The type.
     * @return The argument value.
     */
    public <V> @Nullable V as(@NotNull Class<V> type) {
        if (this.value == null) {
            return null;
        }
        return type.cast(this.value);
    }

    /**
     * Returns the argument value, additionally casting it to the specified type.
     * If the argument value is empty, returns the default value.
     *
     * @param type The type.
     * @param def  The default value.
     * @param <V>  The type.
     * @return The argument value.
     */
    public <V> @Nullable V as(@NotNull Class<V> type, @NotNull V def) {
        if (this.value == null) {
            return def;
        }
        return type.cast(this.value);
    }

    /**
     * Attempts to join the arguments into a single string.
     *
     * @return The joined string or null if the argument is not present.
     */
    public @Nullable String asJoinedString() {
        return this.context.joinedStrings(this.name);
    }

    /**
     * Attempts to join the arguments into a single string.
     *
     * @param def The default value.
     * @return The joined string or null if the argument is not present.
     */
    public @NotNull String asJoinedString(@NotNull String def) {
        String string = this.asJoinedString();
        return string == null ? def : string;
    }
}
