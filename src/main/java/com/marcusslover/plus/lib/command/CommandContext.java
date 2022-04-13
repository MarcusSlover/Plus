package com.marcusslover.plus.lib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record CommandContext(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
    @NotNull
    public CommandContext asPlayer(@NotNull Consumer<Player> player) {
        if (sender instanceof Player player1) player.accept(player1);
        return this;
    }
}
