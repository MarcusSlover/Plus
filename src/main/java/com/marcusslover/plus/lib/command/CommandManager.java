package com.marcusslover.plus.lib.command;

import com.marcusslover.plus.lib.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class CommandManager {
    private final @NotNull Set<org.bukkit.command.Command> commandSet = new HashSet<>();

    @NotNull
    private final Plugin plugin;

    public CommandManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Deprecated
    public static @NotNull CommandManager get(@NotNull Plugin plugin) {
        return new CommandManager(plugin);
    }

    public @NotNull CommandManager register(@NotNull ICommand command) {
        Command commandAnnotation = this.getCommandAnnotation(command);
        if (commandAnnotation == null) {
            return this;
        }
        String name = commandAnnotation.name();
        String description = commandAnnotation.description();
        String permission = commandAnnotation.permission();
        String pMessage = commandAnnotation.permissionMessage().isEmpty() ? Bukkit.getServer().getPermissionMessage() : commandAnnotation.permissionMessage();
        List<String> aliases = Arrays.stream(commandAnnotation.aliases()).toList();

        CommandMap commandMap = Bukkit.getCommandMap();
        org.bukkit.command.Command cmd = new org.bukkit.command.Command(name, description, "", aliases) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                CommandContext commandContext = new CommandContext(sender, commandLabel, args);

                if (!permission.isEmpty() && !sender.hasPermission(permission)) {
                    Text.of(pMessage).send(sender);
                    return false;
                }

                return command.execute(commandContext);
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
                TabCompleteContext tabCompleteContext = new TabCompleteContext(sender, args);

                if (!permission.isEmpty() && !sender.hasPermission(permission)) {
                    return Collections.emptyList();
                }

                return command.tab(tabCompleteContext);
            }
        };
        this.commandSet.add(cmd);
        commandMap.register(name, this.plugin.getName().toLowerCase(Locale.ROOT), cmd);
        return this;
    }

    @NotNull
    public Set<org.bukkit.command.Command> getCommands() {
        return this.commandSet;
    }

    private @Nullable Command getCommandAnnotation(@NotNull ICommand command) {
        Class<? extends @NotNull ICommand> commandClass = command.getClass();
        Command[] annotationsByType = commandClass.getAnnotationsByType(Command.class);
        if (annotationsByType.length >= 1) {
            return annotationsByType[0];
        }
        return null;
    }

    public void clearCommands() {
        CommandMap commandMap = Bukkit.getCommandMap();
        List<String> keys = new ArrayList<>();
        commandMap.getKnownCommands().forEach((key, value) -> {
            if (this.commandSet.contains(value)) {
                keys.add(key);
            }
        });
        for (String key : keys) {
            commandMap.getKnownCommands().remove(key);
        }
    }
}
