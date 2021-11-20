package me.marcusslover.plus.lib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public record CommandContext(CommandSender sender, String label, String[] args) {
    public CommandContext asPlayer(Consumer<Player> player) {
        if (sender instanceof Player player1) player.accept(player1);
        return this;
    }
}
