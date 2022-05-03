package com.marcusslover.plus.lib.command;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * The implementation class must have the {@link Command} annotation above the class.
 */
public interface ICommand {
    boolean execute(@NotNull CommandContext commandContext);

    default List<String> tab(@NotNull TabCompleteContext tabContext) {
        return Collections.emptyList();
    }
}
