package com.marcusslover.plus.lib.text;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ColorUtil {
    public static final Pattern HEX = Pattern.compile("&#([a-fA-F0-9]{6})");
    public static final char COLOR_CHAR = '\u00A7';

    private ColorUtil() {
    }

    public static @NotNull String hex(@NotNull String text) {
        return hexColorization(text);
    }

    public static @NotNull String color(@NotNull String textToTranslate) {
        return color('&', textToTranslate);
    }

    public static @NotNull String color(char altColorChar, @NotNull String textToTranslate) {
        Validate.notNull(textToTranslate, "Cannot translate null text");

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    private static @NotNull String hexColorization(@NotNull String text) {
        var matcher = HEX.matcher(text);
        while (matcher.find()) {
            var bukkitColor = new StringBuilder("&x");
            char[] chars = matcher.group(1).toCharArray();
            for (char character : chars) {
                bukkitColor.append("&").append(character);
            }
            text = text.replaceAll(matcher.group(), String.valueOf(bukkitColor));
        }
        return text;
    }

}
