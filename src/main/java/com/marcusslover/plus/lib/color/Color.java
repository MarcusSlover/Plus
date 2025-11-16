package com.marcusslover.plus.lib.color;

import com.marcusslover.plus.lib.text.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Color {
    private final int rgb;

    public Color(@NotNull java.awt.Color javaColor) {
        this(javaColor.getRGB());
    }

    public Color(@NotNull org.bukkit.Color bukkitColor) {
        this(bukkitColor.asRGB());
    }

    public Color(@NotNull String hex) {
        this(Integer.decode(hex));
    }

    public Color(int rgb) {
        this.rgb = rgb;
    }

    public Color(int red, int green, int blue) {
        int rgb = red;
        rgb = (rgb << 8) + green;
        rgb = (rgb << 8) + blue;
        this.rgb = rgb;
    }

    /**
     * {@link java.awt.Color#HSBtoRGB(float, float, float)}
     */
    public Color(float hue, float saturation, float brightness) {
        this(java.awt.Color.HSBtoRGB(hue, saturation, brightness));
    }

    public Color(TextColor adventureColor) {
        this(adventureColor.value());
    }

    public static @NotNull Color of(@NotNull java.awt.Color javaColor) {
        return new Color(javaColor);
    }

    public static @NotNull Color of(@NotNull org.bukkit.Color bukkitColor) {
        return new Color(bukkitColor);
    }

    public static @NotNull Color of(@NotNull TextColor adventureColor) {
        return new Color(adventureColor);
    }

    public static @NotNull Color of(@NotNull String hex) {
        return new Color(hex);
    }

    public static @NotNull Color of(int red, int green, int blue) {
        return new Color(red, green, blue);
    }

    public static @NotNull Color of(float hue, float saturation, float brightness) {
        return new Color(hue, saturation, brightness);
    }

    public static @NotNull Color of(int rgb) {
        return new Color(rgb);
    }

    public int rgb() {
        return this.rgb;
    }

    public int red() {
        return (this.rgb >> 16) & 0xFF;
    }

    public int green() {
        return (this.rgb >> 8) & 0xFF;
    }

    public int blue() {
        return (this.rgb) & 0xFF;
    }

    public int alpha() {
        return (this.rgb >> 24) & 0xff;
    }

    /**
     * @return the hex value of the color #RRGGBB
     */
    public @NotNull String hex() {
        return String.format("#%02X%02X%02X", this.red(), this.green(), this.blue());
    }

    /**
     * @return The hex color as a format plus recognizes
     */
    public @NotNull String plus() {
        return "&".concat(this.hex());
    }

    /**
     * You will need to use {@link Component#text(String)}.color(color.adventure()) and then serialize it {@link LegacyComponentSerializer#legacySection()}
     *
     * @return The Adventure API {@link TextColor}
     */
    public @NotNull TextColor adventure() {
        return TextColor.color(this.red(), this.green(), this.blue());
    }

    /**
     * @return The Bukkit API {@link org.bukkit.Color}
     */
    public @NotNull org.bukkit.Color bukkit() {
        return org.bukkit.Color.fromRGB(this.red(), this.green(), this.blue());
    }

    /**
     * @return The Java AWT {@link java.awt.Color}
     */
    public @NotNull java.awt.Color java() {
        return new java.awt.Color(this.red(), this.green(), this.blue());
    }

    /**
     * A hacky way of pre-converting the color into a bukkit translatable color.
     * <b>Note that this pretty much only works with {@link Player#sendMessage(String)}</b>
     *
     * @return the converted color as a String
     */
    public @NotNull String legacy() {
        return ColorUtil
            .legacySection()
            .serialize(Component.text(" ").color(this.adventure()))
            .trim();
    }

    /**
     * Creates a darker color.
     * Take in mind, this function does not change values of the original color instance.
     * Instead, it creates a completely new instance of color for you.
     *
     * @param value Darkness multiplier, value between 0.0 and 1.0.
     * @return New, darker color.
     */
    public @NotNull Color darker(double value) {
        return new Color(
            Math.max((int) (this.red() * value), 0),
            Math.max((int) (this.green() * value), 0),
            Math.max((int) (this.blue() * value), 0)
        );
    }

    /**
     * Creates a brighter color.
     * Take in mind, this function does not change values of the original color instance.
     * Instead, it creates a completely new instance of color for you.
     *
     * @param value Brightness multiplier, value between 0.0 and 1.0.
     * @return New, brighter color.
     */
    public @NotNull Color brighter(double value) {
        int r = this.red();
        int g = this.green();
        int b = this.blue();

        int i = (int) (1.0 / (1.0 - value));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }

        return new Color(
            Math.min((int) (r / value), 255),
            Math.min((int) (g / value), 255),
            Math.min((int) (b / value), 255)
        );
    }

    @Override
    public String toString() {
        return "Color{" +
            "rgb=" + this.rgb +
            '}';
    }
}
