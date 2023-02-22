package com.marcusslover.plus.lib.command;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * The implementation class must have the {@link Command} annotation above the class.
 */
public interface ICommand extends ITabCompleterHelper {
    boolean execute(@NotNull CommandContext cmd);

    /**
     * Called when the command is registered.
     * Allows you to build a specific structure for the command.
     * @param builder An empty command builder.
     */
    default void onRegister(@NotNull CommandBuilder builder) {

    }

    default @NotNull List<@NotNull String> tab(@NotNull TabCompleteContext tab) {
        return Collections.emptyList();
    }
}
