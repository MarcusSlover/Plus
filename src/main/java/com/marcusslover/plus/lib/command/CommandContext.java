package com.marcusslover.plus.lib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record CommandContext(@NotNull CommandSender sender, @NotNull String label,
                             @NotNull String[] args) implements ICommandContextHelper<CommandContext> {
    public @NotNull CommandContext asPlayer(@NotNull Consumer<@NotNull Player> player) {
        if (sender instanceof Player p) player.accept(p);
        return this;
    }

    public @NotNull CommandContext asConsole(@NotNull Consumer<ConsoleCommandSender> console) {
        if (sender instanceof ConsoleCommandSender c) console.accept(c);
        return this;
    }
}
