package com.marcusslover.plus.lib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * The context of a command.
 * Mainly used by developers in the {@link ICommand#execute(CommandContext)} method.
 * Provides you with a context of a specific command execution.
 *
 * @param sender The sender of the command.
 * @param label  The label of the command.
 * @param args   The arguments of the command.
 * @param parent The parent context.
 */
public record CommandContext(@NotNull Command commandData,
                             @NotNull CommandSender sender,
                             @NotNull String label,
                             @NotNull String[] args,
                             @Nullable CommandContext parent) implements ICommandContextHelper<CommandContext> {

    /**
     * Creates a new command context.
     *
     * @param sender The sender of the command.
     * @param label  The label of the command.
     * @param args   The arguments of the command.
     */
    public CommandContext(@NotNull Command commandData, @NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        this(commandData, sender, label, args, null);
    }

    /**
     * Gets the sender of the command.
     * Throws an exception if the sender is not a player.
     *
     * @return The sender of the command.
     */
    public @NotNull Player player() {
        if (this.sender instanceof Player p) {
            return p;
        }
        throw new IllegalStateException("Sender is not a player");
    }

    /**
     * Gets the sender of the command.
     * Throws an exception if the sender is not a console.
     *
     * @return The sender of the command.
     */
    public @NotNull ConsoleCommandSender console() {
        if (this.sender instanceof ConsoleCommandSender c) {
            return c;
        }
        throw new IllegalStateException("Sender is not a console");
    }

    /**
     * Gets the sender of the command.
     * Passes the sender to the consumer if it is a player.
     *
     * @param player The consumer to pass the player to.
     * @return The context.
     */
    public @NotNull CommandContext asPlayer(@NotNull Consumer<@NotNull Player> player) {
        if (this.sender instanceof Player p) {
            player.accept(p);
        }
        return this;
    }

    /**
     * Gets the sender of the command.
     * Passes the sender to the consumer if it is a console.
     *
     * @param console The consumer to pass the console to.
     * @return The context.
     */
    public @NotNull CommandContext asConsole(@NotNull Consumer<ConsoleCommandSender> console) {
        if (this.sender instanceof ConsoleCommandSender c) {
            console.accept(c);
        }
        return this;
    }

    /**
     * Creates a child context.
     *
     * @param consumedArguments The amount of consumed arguments
     * @return A child context with this context as the parent, the last consumed argument as the label,
     * and all unconsumed arguments as args.
     * Created by tecc
     */
    public @NotNull CommandContext child(int consumedArguments) {
        List<String> originalArgs = List.of(this.args());
        if (originalArgs.size() < 1) {
            throw new IndexOutOfBoundsException("At least one argument must be available to make a child context");
        }

        String label = originalArgs.get(consumedArguments - 1);
        CommandSender sender = this.sender();
        int remainingArgsLength = originalArgs.size() - consumedArguments;
        List<String> remainingArgs = remainingArgsLength > 0 ? originalArgs.subList(consumedArguments, originalArgs.size()) : Collections.emptyList();

        // 03.25.2023 - MarcusSlover: Added this.commandData to the constructor.
        return new CommandContext(this.commandData, sender, label, remainingArgs.toArray(new String[0]), this);
    }
}
