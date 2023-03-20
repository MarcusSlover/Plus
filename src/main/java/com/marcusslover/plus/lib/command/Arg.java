package com.marcusslover.plus.lib.command;

import com.marcusslover.plus.lib.command.ArgTypes.Deserializer;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Represents an argument of a command.
 */
@Data
@Accessors(fluent = true, chain = true)
public class Arg<T> {
    /**
     * The types of the arguments.
     */
    public static final ArgTypes TYPES = new ArgTypes()
            /* Bukkit */
            .register((Deserializer<Player>) Bukkit::getPlayer)
            /* Numbers */
            .register((Deserializer<Integer>) Integer::parseInt)
            .register((Deserializer<Double>) Double::parseDouble)
            .register((Deserializer<Float>) Float::parseFloat)
            .register((Deserializer<Long>) Long::parseLong)
            .register((Deserializer<Short>) Short::parseShort)
            .register((Deserializer<Byte>) Byte::parseByte)
            /* Other */
            .register((Deserializer<Boolean>) Boolean::parseBoolean)
            .register((Deserializer<String>) s -> s);

    /**
     * The name of the argument.
     */
    private final @NotNull String name;

    /**
     * The type of the argument.
     */
    private final @NotNull Class<?> type;

    /**
     * The children of the argument.
     */
    private final @NotNull LinkedList<Arg<?>> children = new LinkedList<>();

    /**
     * The executor of the command.
     */
    private @Nullable Consumer<CommandContext> executor;

    /**
     * Creates a new argument.
     *
     * @param name The name of the argument.
     * @param type The type of the argument.
     * @param <V>  The type of the argument.
     * @return The new argument.
     */
    public static <V> @NotNull Arg<V> of(@NotNull String name, @NotNull Class<V> type) {
        return new Arg<>(name, type);
    }

    /**
     * Creates a new literal argument.
     *
     * @param name The name of the argument.
     * @return The new argument.
     */
    public static @NotNull Arg<String> literal(@NotNull String name) {
        return of(name, String.class);
    }

    /**
     * Creates a new argument.
     *
     * @param arg The new child argument.
     * @return The parent argument.
     */
    public @NotNull Arg<T> then(@NotNull Arg<?> arg) {
        this.children.add(arg);
        return this;
    }
}
