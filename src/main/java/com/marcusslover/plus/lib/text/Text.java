package com.marcusslover.plus.lib.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class Text {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    @NotNull
    protected String text;

    @NotNull
    protected Component component;

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

    @NotNull
    public static Text of(@NotNull String text) {
        return new Text(text);
    }

    @NotNull
    public static Text of(@NotNull Component component) {
        return new Text(component);
    }

    @NotNull
    public static List<Text> list(@NotNull List<Component> lore) {
        return lore.stream().map(Text::new).collect(Collectors.toList());
    }

    @NotNull
    public static String legacy(@NotNull String text) {
        return ColorUtil.color('&', text);
    }

    @NotNull
    public static Text empty() {
        return new Text("");
    }

    @NotNull
    public static Text reset() {
        return new Text("&f");
    }

    @NotNull
    public Text setHover(@Nullable Text hover) {
        if (hover == null) {
            this.component = this.component.hoverEvent(null);
        } else {
            this.component = component.hoverEvent(HoverEvent.showText(hover.component));
        }
        return this;
    }

    @NotNull
    public String raw() {
        return text;
    }

    public boolean isEmpty() {
        if (component instanceof TextComponent textComponent) {
            return textComponent.content().isEmpty();
        }
        return true;
    }

    @NotNull
    public Component comp() {
        return component;
    }

    @NotNull
    public Text send(@NotNull CommandSender sender) {
        sender.sendMessage(comp());
        return this;
    }

}
