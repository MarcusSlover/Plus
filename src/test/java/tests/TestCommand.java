package tests;

import com.marcusslover.plus.lib.command.Arg;
import com.marcusslover.plus.lib.command.Command;
import com.marcusslover.plus.lib.command.CommandBuilder;
import com.marcusslover.plus.lib.command.CommandContext;
import com.marcusslover.plus.lib.command.ICommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Command(name = "test")
public class TestCommand implements ICommand {
    @Override
    public boolean execute(@NotNull CommandContext cmd) {
        return false;
    }

    @Override
    public void onRegister(@NotNull CommandBuilder builder) {
        builder.register(Arg.literal("heal")
                .then(Arg.of("player", Player.class)
                        .executor(cmd -> {
                            @Nullable Player player = cmd.arg("player").as(Player.class);
                            String text = cmd.arg("text").asJoinedString("");
                        })
                ).executor(cmd -> {
                    Player player = cmd.player();
                    player.setHealth(20);
                })
        );
    }
}
