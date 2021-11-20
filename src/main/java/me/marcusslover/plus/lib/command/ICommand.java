package me.marcusslover.plus.lib.command;

/**
 * The implementation class must have the {@link Command} annotation above the class.
 */
public interface ICommand {
    boolean execute(CommandContext commandContext);
}
