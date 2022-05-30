package com.marcusslover.plus.lib.command;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ICommandContextHelper<V> {
    @NotNull V asPlayer(@NotNull Consumer<@NotNull Player> player);

    @NotNull V asConsole(@NotNull Consumer<@NotNull ConsoleCommandSender> console);
}
