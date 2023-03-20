package com.marcusslover.plus.lib.title;

import com.marcusslover.plus.lib.common.ISendable;
import com.marcusslover.plus.lib.text.Text;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.util.Ticks;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.title.Title.Times;
import static net.kyori.adventure.title.Title.title;

public class Title implements ISendable<Title> {
    private static final Times DEFAULT_TIMES = Times.times(
            Ticks.duration(10),
            Ticks.duration(40),
            Ticks.duration(10)
    );

    protected final @NotNull Text title;
    protected final @NotNull Text subtitle;
    protected Times times = DEFAULT_TIMES;

    private Title(@NotNull String title) {
        this(title, "");
    }

    private Title(@NotNull Text text) {
        this(text, Text.empty());
    }

    private Title(@NotNull String title, @NotNull String subtitle) {
        this(Text.of(title), Text.of(subtitle));
    }

    private Title(@NotNull Text title, @NotNull Text subtitle) {
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

    public @NotNull Title send(@NotNull CommandSender player, long fadeIn, long fadeStay, long fadeOut) {
        Times of = Times.times(Ticks.duration(fadeIn), Ticks.duration(fadeStay), Ticks.duration(fadeOut));
        player.showTitle(title(
                this.title.comp(),
                this.subtitle.comp(),
                of
        ));

        return this;
    }

    public @NotNull Title send(@NotNull CommandSender player, @Nullable Times times) {
        player.showTitle(title(
                this.title.comp(),
                this.subtitle.comp(),
                times
        ));

        return this;
    }

    @Override
    public @NotNull <T extends CommandSender> Title send(@NotNull T target) {
        target.showTitle(this.adventure());

        return this;
    }

    @Override
    public @NotNull Title send(Audience audience) {
        audience.showTitle(this.adventure());

        return this;
    }

    public @NotNull net.kyori.adventure.title.Title adventure() {
        return title(
                this.title.comp(),
                this.subtitle.comp(),
                this.times
        );
    }

    public @NotNull Title times(@NotNull Times times) {
        this.times = times;
        return this;
    }

    public @NotNull Title times(int fadeIn, int stay, int fadeOut) {
        this.times = Times.times(
                Ticks.duration(fadeIn),
                Ticks.duration(stay),
                Ticks.duration(fadeOut)
        );

        return this;
    }

    public @NotNull Title times(long fadeIn, long stay, long fadeOut) {
        this.times = Times.times(
                Ticks.duration(fadeIn),
                Ticks.duration(stay),
                Ticks.duration(fadeOut)
        );

        return this;
    }
}