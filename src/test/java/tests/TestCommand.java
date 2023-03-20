package tests;

import com.marcusslover.plus.lib.command.Command;
import com.marcusslover.plus.lib.command.CommandContext;
import com.marcusslover.plus.lib.command.ICommand;
import org.jetbrains.annotations.NotNull;

@Command(name = "test")
public class TestCommand implements ICommand {
    @Override
    public boolean execute(@NotNull CommandContext cmd) {
        CommandContext commandContext = new CommandContext(cmd.sender(), cmd.label(), cmd.args(), cmd.parent());

        return false;
    }
}
