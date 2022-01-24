package me.marcusslover.plus.lib.title;

import me.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Title {
    private static final net.kyori.adventure.title.Title.Times TIMES =
            net.kyori.adventure.title.Title.Times.of(
                    Ticks.duration(10),
                    Ticks.duration(40),
                    Ticks.duration(10));

    @NotNull
    protected final Text title;
    @NotNull
    protected final Text subtitle;

    public Title(@NotNull String title) {
        this(title, "");
    }

    public Title(@NotNull Text text) {
        this(text, Text.empty());
    }

    public Title(@NotNull String title, @NotNull String subtitle) {
        this(new Text(title), new Text(subtitle));
    }

    public Title(@NotNull Text title, @NotNull Text subtitle) {

        this.title = title;
        this.subtitle = subtitle;
    }

    @NotNull
    public Title send(@NotNull Player player) {
        return send(player, TIMES);
    }

    @NotNull
    public Title send(@NotNull Player player, long fadeIn, long fadeStay, long fadeOut) {
        net.kyori.adventure.title.Title.Times of = net.kyori.adventure.title.Title.Times.of(Ticks.duration(fadeIn), Ticks.duration(fadeStay), Ticks.duration(fadeOut));
        player.showTitle(net.kyori.adventure.title.Title.title(title.comp(), subtitle.comp(), of));
        return this;
    }

    @NotNull
    public Title send(@NotNull Player player, @Nullable net.kyori.adventure.title.Title.Times times) {
        player.showTitle(net.kyori.adventure.title.Title.title(title.comp(), subtitle.comp(), times));
        return this;
    }


}
