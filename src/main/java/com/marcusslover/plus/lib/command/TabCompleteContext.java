package com.marcusslover.plus.lib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record TabCompleteContext(@NotNull CommandSender sender,
                                 @NotNull String[] args) implements ICommandContextHelper<TabCompleteContext> {
    @Override
    public @NotNull TabCompleteContext asPlayer(@NotNull Consumer<@NotNull Player> player) {
        if (this.sender instanceof Player p) {
            player.accept(p);
        }
        return this;
    }

    @Override
    public @NotNull TabCompleteContext asConsole(@NotNull Consumer<@NotNull ConsoleCommandSender> console) {
        if (this.sender instanceof ConsoleCommandSender c) {
            console.accept(c);
        }
        return this;
    }
}
