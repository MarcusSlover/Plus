package me.marcusslover.plus.lib.color;

import org.jetbrains.annotations.NotNull;

public class Color {
    private final int rgb;

    public Color(@NotNull String hex) {
        this.rgb = Integer.decode(hex);
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

    public int red() {
        return (rgb >> 16) & 0xFF;
    }

    public int green() {
        return (rgb >> 8) & 0xFF;
    }

    public int blue() {
        return (rgb) & 0xFF;
    }

    public int alpha() {
        return (rgb >> 24) & 0xff;
    }

    @NotNull
    public String hex() {
        return "&#" + Integer.toHexString(rgb);
    }

    public Color darker(double value) {
        return new Color(
                Math.max((int) (red() * value), 0),
                Math.max((int) (green() * value), 0),
                Math.max((int) (blue() * value), 0)
        );
    }

    public Color brighter(double value) {
        int r = red();
        int g = green();
        int b = blue();

        int i = (int) (1.0 / (1.0 - value));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(
                Math.min((int) (r / value), 255),
                Math.min((int) (g / value), 255),
                Math.min((int) (b / value), 255)
        );
    }
}
