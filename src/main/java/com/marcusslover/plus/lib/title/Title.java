package com.marcusslover.plus.lib.title;

import com.marcusslover.plus.lib.text.Text;
import com.marcusslover.plus.lib.util.ISendable;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Title implements ISendable<Player, Title> {
    private static final net.kyori.adventure.title.Title.Times TIMES =
            net.kyori.adventure.title.Title.Times.times(
                    Ticks.duration(10),
                    Ticks.duration(40),
                    Ticks.duration(10));

    protected final @NotNull Text title;
    protected final @NotNull Text subtitle;

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

    public static @NotNull Title of(@NotNull String title) {
        return new Title(title);
    }

    public static @NotNull Title of(@NotNull Text text) {
        return new Title(text);
    }

    public static @NotNull Title of(@NotNull String title, @NotNull String subtitle) {
        return new Title(title, subtitle);
    }

    public static @NotNull Title of(@NotNull Text title, @NotNull Text subtitle) {
        return new Title(title, subtitle);
    }

    @Override
    public @NotNull Title send(@NotNull Player player) {
        return send(player, TIMES);
    }

    public @NotNull Title send(@NotNull Player player, long fadeIn, long fadeStay, long fadeOut) {
        net.kyori.adventure.title.Title.Times of = net.kyori.adventure.title.Title.Times.times(Ticks.duration(fadeIn), Ticks.duration(fadeStay), Ticks.duration(fadeOut));
        player.showTitle(net.kyori.adventure.title.Title.title(title.comp(), subtitle.comp(), of));
        return this;
    }

    public @NotNull Title send(@NotNull Player player, @Nullable net.kyori.adventure.title.Title.Times times) {
        player.showTitle(net.kyori.adventure.title.Title.title(title.comp(), subtitle.comp(), times));
        return this;
    }


}
