package com.marcusslover.plus.lib.text;

import com.marcusslover.plus.lib.common.ISendable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Text implements ISendable<CommandSender, Text> {
    private static final @NotNull LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    protected @NotNull String text;

    protected @NotNull Component component;

    public Text() {
        this("");
    }

    public Text(@NotNull String text) {
        // Colorize the raw text before deserialization
        this(ColorUtil.hex(text), LEGACY.deserialize(ColorUtil.hex(text)));
    }

    public Text(@NotNull Component component) {
        this(LEGACY.serialize(component), component);
    }

    public Text(@NotNull String text, @NotNull Component component) {
        this.text = text;
        this.component = component.decoration(TextDecoration.ITALIC, false);
    }

    public static @NotNull Text of(@NotNull String text) {
        return new Text(text);
    }

    public static @NotNull Text of(@NotNull Component component) {
        return new Text(component);
    }

    @Deprecated
    public static @NotNull List<@NotNull Text> list(@NotNull List<@NotNull Component> lore) {
        return lore.stream().map(Text::new).collect(Collectors.toList());
    }

    public static @NotNull String legacy(@NotNull String text) {
        return ColorUtil.color('&', text);
    }

    public static @NotNull Text empty() {
        return new Text("");
    }

    public static @NotNull Text reset() {
        return new Text("&f");
    }

    public @NotNull Text hover(@Nullable Text hover) {
        if (hover == null) {
            this.component = this.component.hoverEvent(null);
        } else {
            this.component = this.component.hoverEvent(HoverEvent.showText(hover.component));
        }
        return this;
    }

    public @NotNull Text click(@Nullable ClickEvent click) {
        this.component = this.component.clickEvent(click);
        return this;
    }

    public @NotNull Text append(@NotNull String text) {
        return this.append(new Text(text));
    }

    public @NotNull Text append(@NotNull Text text) {
        this.text += text.text;
        this.component = this.component.append(text.component);
        return this;
    }

    public @NotNull String raw() {
        return this.text;
    }

    public @NotNull String stripped() {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.raw()));
    }

    public boolean isEmpty() {
        if (this.component instanceof TextComponent textComponent) {
            return textComponent.content().isEmpty();
        }
        return true;
    }

    public @NotNull Component comp() {
        return this.component;
    }

    public @NotNull Text copy() {
        return new Text(this.text, this.component);
    }

    public @NotNull Text send(@NotNull CommandSender sender) {
        sender.sendMessage(this.comp());
        return this;
    }

    @Override
    public @NotNull Text send(@NotNull CommandSender... targets) {
        for (CommandSender target : targets) {
            this.send(target);
        }

        return this;
    }

    @Override
    public @NotNull Text send(@NotNull Collection<CommandSender> targets) {
        targets.forEach(this::send);

        return this;
    }

    public @NotNull Text sendActionBar(@NotNull CommandSender sender) {
        sender.sendActionBar(this.comp());
        return this;
    }

    public @NotNull <T extends CommandSender> Text sendActionBar(@NotNull Collection<T> players) {
        for (T sender : players) {
            this.sendActionBar(sender);
        }

        return this;
    }

    public @NotNull <T extends CommandSender> Text sendActionBar(@NotNull T... players) {
        for (T sender : players) {
            this.sendActionBar(sender);
        }

        return this;
    }

    @Override
    public String toString() {
        return LEGACY.serialize(this.component);
    }
}
