package com.marcusslover.plus.lib.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple tab completer helper for tab completion.
 *
 * @author MarcusSlover
 */
public interface ITabCompleterHelper {
    /**
     * Gets the tab complete for the argument.
     *
     * @param arg         The argument.
     * @param subCommands The sub commands.
     * @return The tab completes.
     */
    default List<String> tabComplete(String arg, List<String> subCommands) {
        return subCommands.stream().filter(x -> x.toLowerCase().startsWith(arg.toLowerCase())).toList();
    }

    /**
     * Gets a list of all player names.
     *
     * @return The list of all player names.
     */
    default @NotNull List<String> playerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}