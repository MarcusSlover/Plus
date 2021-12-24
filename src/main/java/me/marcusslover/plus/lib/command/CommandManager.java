package me.marcusslover.plus.lib.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CommandManager {
    private static CommandManager instance;

    private CommandManager() {
        instance = this;
    }

    public static CommandManager get() {
        return instance == null ? new CommandManager() : instance;
    }

    public CommandManager register(@NotNull ICommand command) {
        return this.register("plus", command);
    }

    public CommandManager register(@NotNull String prefix, @NotNull ICommand command) {
        Command commandAnnotation = getCommandAnnotation(command);
        if (commandAnnotation == null) return this;
        String name = commandAnnotation.name();
        String description = commandAnnotation.description();
        List<String> aliases = Arrays.stream(commandAnnotation.aliases()).toList();

        CommandMap commandMap = Bukkit.getCommandMap();
        commandMap.register(commandAnnotation.name(), prefix, new org.bukkit.command.Command(
                name, description, "", aliases) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                CommandContext commandContext = new CommandContext(sender, commandLabel, args);
                return command.execute(commandContext);
            }
        });
        return this;
    }

    private @Nullable Command getCommandAnnotation(@NotNull ICommand command) {
        Class<? extends @NotNull ICommand> commandClass = command.getClass();
        Command[] annotationsByType = commandClass.getAnnotationsByType(Command.class);
        if (annotationsByType.length >= 1) {
            return annotationsByType[0];
        }
        return null;
    }
}
