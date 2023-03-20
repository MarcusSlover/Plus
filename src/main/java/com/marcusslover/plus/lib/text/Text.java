package com.marcusslover.plus.lib.text;

import com.marcusslover.plus.lib.color.ColorGradient;
import com.marcusslover.plus.lib.common.ISendable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Text implements ISendable<Text> {
    private static final @NotNull LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    protected @NotNull String text;
    protected @NotNull Component component;

    private Text(@NotNull String text) {
        // Colorize the raw text before deserialization
        this(ColorUtil.hex(text), LEGACY.deserialize(ColorUtil.hex(text)));
    }

    private Text(@NotNull Component component) {
        this(LEGACY.serialize(component), component);
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

    /**
     * Creates a gradient text.
     *
     * @param text     the text to be colored.
     * @param gradient the gradient to be used.
     * @return the gradient text.
     */
    public static @NotNull String gradient(@NotNull String text, @NotNull ColorGradient gradient) {
        int length = text.length();
        StringBuilder builder = new StringBuilder();
        gradient.forEach(length, color -> builder.append(color.plus()).append(text.charAt(0)));
        return builder.toString();
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

    public @NotNull Text append(@NotNull Component component) {
        return this.append(new Text(component));
    }

    public @NotNull Text append(@NotNull Text text) {
        this.text += text.text;
        this.component = this.component.append(text.component);
        return this;
    }

    public @NotNull Text font(@NotNull Key font) {
        this.component = this.component.font(font);
        return this;
    }

    public @NotNull String raw() {
        return this.text;
    }

    public @NotNull String stripped() {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.raw()));
    }

    public @NotNull String legacy() {
        return Text.legacy(this.text);
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

    @Override
    public @NotNull <T extends CommandSender> Text send(@NotNull T target) {
        target.sendMessage(this.comp());
        return this;
    }

    @Override
    public @NotNull Text send(Audience audience) {
        audience.sendMessage(this.comp());

        return this;
    }

    public @NotNull Text sendActionBar(@NotNull Audience audience) {
        audience.sendActionBar(this.comp());
        return this;
    }

    public @NotNull <T extends CommandSender> Text sendActionBar(@NotNull T target) {
        target.sendActionBar(this.comp());
        return this;
    }

    public @NotNull <T extends CommandSender> Text sendActionBar(@NotNull Collection<T> targets) {
        for (T sender : targets) {
            this.sendActionBar(sender);
        }

        return this;
    }

    public @NotNull <T extends CommandSender> Text sendActionBar(@NotNull T... targets) {
        for (T sender : targets) {
            this.sendActionBar(sender);
        }

        return this;
    }

    @Override
    public String toString() {
        return LEGACY.serialize(this.component);
    }
}
