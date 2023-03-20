package com.marcusslover.plus.lib.command;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to create a command.
 */
@Data
public class CommandBuilder {
    private boolean empty = true; // indicates if the command is empty
    /**
     * The root argument.
     */
    private @Nullable Arg<?> root;

    /**
     * Registers the root argument.
     *
     * @param arg The root argument.
     * @return The command builder.
     */
    public @NotNull CommandBuilder register(@NotNull Arg<?> arg) {
        this.root = arg;
        this.setEmpty(false);
        return this;
    }
}
