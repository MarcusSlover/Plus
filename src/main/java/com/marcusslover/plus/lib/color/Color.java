package com.marcusslover.plus.lib.color;

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

    public static @NotNull Color of(@NotNull java.awt.Color javaColor) {
        return new Color(javaColor);
    }

    public static @NotNull Color of(@NotNull org.bukkit.Color bukkitColor) {
        return new Color(bukkitColor);
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
        return rgb;
    }

    public int getRGB() {
        return rgb;
    }

    public int red() {
        return (rgb >> 16) & 0xFF;
    }

    public int getRed() {
        return red();
    }

    public int green() {
        return (rgb >> 8) & 0xFF;
    }

    public int getGreen() {
        return green();
    }

    public int blue() {
        return (rgb) & 0xFF;
    }

    public int getBlue() {
        return blue();
    }

    public int alpha() {
        return (rgb >> 24) & 0xff;
    }

    public int getAlpha() {
        return alpha();
    }

    public @NotNull String hex() {
        return "&#" + Integer.toHexString(rgb);
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
                Math.max((int) (red() * value), 0),
                Math.max((int) (green() * value), 0),
                Math.max((int) (blue() * value), 0)
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
        int r = red();
        int g = green();
        int b = blue();

        int i = (int) (1.0 / (1.0 - value));
        if (r == 0 && g == 0 && b == 0) return new Color(i, i, i);
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(
                Math.min((int) (r / value), 255),
                Math.min((int) (g / value), 255),
                Math.min((int) (b / value), 255)
        );
    }

    @Override
    public String toString() {
        return "Color{" +
                "rgb=" + rgb +
                '}';
    }
}
