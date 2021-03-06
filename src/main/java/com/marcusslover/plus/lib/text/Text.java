package com.marcusslover.plus.lib.text;

import com.marcusslover.plus.lib.util.ISendable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public @NotNull Text setHover(@Nullable Text hover) {
        if (hover == null) {
            this.component = this.component.hoverEvent(null);
        } else {
            this.component = component.hoverEvent(HoverEvent.showText(hover.component));
        }
        return this;
    }

    public @NotNull Text setClick(@Nullable ClickEvent click) {
        this.component = component.clickEvent(click);
        return this;
    }

    public @NotNull String raw() {
        return text;
    }

    public boolean isEmpty() {
        if (component instanceof TextComponent textComponent) {
            return textComponent.content().isEmpty();
        }
        return true;
    }

    public @NotNull Component comp() {
        return component;
    }

    public @NotNull Text send(@NotNull CommandSender sender) {
        sender.sendMessage(comp());
        return this;
    }

}
