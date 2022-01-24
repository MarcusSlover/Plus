package me.marcusslover.plus.lib.command;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation class must have the {@link Command} annotation above the class.
 */
public interface ICommand {
    boolean execute(@NotNull CommandContext commandContext);
}
