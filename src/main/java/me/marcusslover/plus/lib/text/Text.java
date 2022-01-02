package me.marcusslover.plus.lib.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class Text {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    protected String text;
    protected Component component;

    public Text() {
        this("");
    }

    public Text(String text) {
        // Colorize the raw text before deserialization
        this(ColorUtil.hex(text), LEGACY.deserialize(ColorUtil.hex(text)));
    }

    public Text(Component component) {
        this(LEGACY.serialize(component), component);
    }

    public Text(String text, Component component) {
        this.text = text;
        this.component = component.decoration(TextDecoration.ITALIC, false);
    }

    @NotNull
    public static List<Text> list(@NotNull List<Component> lore) {
        return lore.stream().map(Text::new).collect(Collectors.toList());
    }

    public static String legacy(String text) {
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

    public Text setHover(Text hover) {
        component.hoverEvent(HoverEvent.showText(hover.component));
        return this;
    }

    public String raw() {
        return text;
    }

    public boolean isEmpty() {
        if (component instanceof TextComponent textComponent) {
            return textComponent.content().isEmpty();
        }
        return true;
    }

    public Component comp() {
        return component;
    }

    @NotNull
    public Text send(@NotNull CommandSender sender) {
        sender.sendMessage(comp());
        return this;
    }

}
