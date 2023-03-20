package com.marcusslover.plus.lib.command;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The argument context. Parsed arguments are stored here.
 */
@Data
@Accessors(fluent = true)
public class ArgContext {
    /**
     * The parsed values.
     */
    private final @NotNull LinkedHashMap<String, Object> parsedValues;

    /**
     * Get the argument by name.
     *
     * @param name The name of the argument.
     * @return The value of the argument.
     */
    public @Nullable Object provide(@NotNull String name) {
        return this.parsedValues.get(name);
    }

    /**
     * In a case of a command with multiple arguments that represent a single string.
     * This method will join the strings and return the joined string.
     *
     * @param name The name of the argument to start from.
     * @return The joined string or null if the argument is not present.
     */
    public @Nullable String joinedStrings(@NotNull String name) {
        // If the argument is not present, return null.
        if (!this.has(name)) {
            return null;
        }

        // If the argument is the only one, return the value.
        if (this.parsedValues.size() == 1) {
            return (String) this.provide(name);
        }

        // Attempt joining the strings.
        long count = this.parsedValues.entrySet().stream().takeWhile(entry -> !entry.getKey().equals(name)).count();
        int startingIndex = (int) count;
        int endingIndex = this.parsedValues.size() - 1;

        StringBuilder builder = new StringBuilder();
        // Loop from the starting index to the ending index.
        for (int i = startingIndex; i <= endingIndex; i++) {
            Map.Entry<String, Object> entry = this.parsedValues.entrySet().stream().skip(i).findFirst().orElse(null);
            if (entry == null) {
                continue;
            }
            // Append the value to the builder.
            String value = (String) entry.getValue();
            builder.append(value).append(" ");
        }

        // Return the joined string.
        return builder.toString().trim();
    }

    /**
     * Indicates if the argument is present.
     *
     * @param name The name of the argument.
     * @return True if the argument is present.
     */
    public boolean has(@NotNull String name) {
        return this.parsedValues.containsKey(name);
    }
}
