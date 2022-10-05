package com.marcusslover.plus.lib.color;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ColorHelper {
    private static final Pattern HEX_PATTERN = Pattern.compile("^#([a-fA-F0-9]{6})$");
    private static final HashMap<DyeColor, ChatColor> dyeChatMap = new HashMap<>(16);

    static {
        dyeChatMap.put(DyeColor.BLACK, ChatColor.BLACK);
        dyeChatMap.put(DyeColor.BLUE, ChatColor.DARK_BLUE);
        dyeChatMap.put(DyeColor.BROWN, ChatColor.GOLD);
        dyeChatMap.put(DyeColor.CYAN, ChatColor.AQUA);
        dyeChatMap.put(DyeColor.GRAY, ChatColor.DARK_GRAY);
        dyeChatMap.put(DyeColor.GREEN, ChatColor.DARK_GREEN);
        dyeChatMap.put(DyeColor.LIGHT_BLUE, ChatColor.BLUE);
        dyeChatMap.put(DyeColor.LIME, ChatColor.GREEN);
        dyeChatMap.put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE);
        dyeChatMap.put(DyeColor.ORANGE, ChatColor.GOLD);
        dyeChatMap.put(DyeColor.PINK, ChatColor.LIGHT_PURPLE);
        dyeChatMap.put(DyeColor.PURPLE, ChatColor.DARK_PURPLE);
        dyeChatMap.put(DyeColor.RED, ChatColor.DARK_RED);
        dyeChatMap.put(DyeColor.LIGHT_GRAY, ChatColor.GRAY);
        dyeChatMap.put(DyeColor.WHITE, ChatColor.WHITE);
        dyeChatMap.put(DyeColor.YELLOW, ChatColor.YELLOW);
    }

    public static ChatColor dyeToChat(DyeColor dye) {
        if (dyeChatMap.containsKey(dye)) {
            return dyeChatMap.get(dye);
        }
        return ChatColor.MAGIC;
    }


    /**
     * Convert color codes and hex codes to Color container (RGB Container)
     *
     * @param value Must match one of these formats "#FFFFFF" or "&c"
     * @return org.bukkit.Color
     */
    public static Color getRGBFromCode(String value) {
        return HEX_PATTERN.matcher(value).matches() ? hex2Rgb(value) : minecraft2Rgb(value);
    }

    /**
     * Convert string to a ChatColor object
     *
     * @param value Must match one of these formats "#FFFFFF" or "&c"
     * @return net.md_5.bungee.api.ChatColor
     */
    public static ChatColor getChatColorFromCode(String value) {
        if (value.matches("&([A-z0-9])")) {
            if (value.length() <= 1) {
                return ChatColor.WHITE;
            }
            return ChatColor.getByChar(value.charAt(1));
        } else if (HEX_PATTERN.matcher(value).matches()) {
            return ChatColor.of(value);
        } else {
            return ChatColor.WHITE;
        }
    }

    /**
     * Convert hex color code to default chat color (this is a bit finicky)
     *
     * @param hex Must match one of these formats "#FFFFFF" or "&c"
     * @return net.md_5.bungee.api.ChatColor
     */
    public static ChatColor getChatColorFromHex(String hex) {
        return dyeToChat(DyeColor.getByColor(hex2Rgb(hex)));
    }

    /**
     * Convert a string to a Color object
     *
     * @param value     A properly formatted "R,G,B" string
     * @param separator Separator used to form the "R,G,B" string
     * @return org.bukkit.Color
     */
    public static Color rgbFromString(String value, String separator) {
        String[] split = value.split(Pattern.quote(separator));
        return Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    /**
     * Convert a string to a Color object
     *
     * @param value A properly formatted "R G B" string
     * @return org.bukkit.Color
     */
    public static Color rgbFromString(String value) {
        return rgbFromString(value, " ");
    }

    /**
     * Convert a Color object to "R,G,B" readable format
     *
     * @param color     org.bukkit.Color
     * @param separator Separator used to form the "R,G,B" string
     * @return A readable "R,G,B" string
     */
    public static String rgbToString(Color color, String separator) {
        return separator + color.getGreen() + separator + color.getBlue();
    }

    /**
     * Convert a Color object to "R G B" readable format
     *
     * @param color org.bukkit.Color
     * @return A readable "R G B" string
     */
    public static String rgbToString(Color color) {
        return rgbToString(color, " ");
    }


    /**
     * Converts "#FFFFFF" format to a Color object containing RGB values
     *
     * @param value Must be in format "#FFFFFF"
     * @return org.bukkit.Color
     */
    public static Color hex2Rgb(String value) {
        try {
            Color color;
            if (value.contains("#")) {
                color = Color.fromRGB(
                        Integer.valueOf(value.substring(1, 3), 16),
                        Integer.valueOf(value.substring(3, 5), 16),
                        Integer.valueOf(value.substring(5, 7), 16));
            } else {
                color = Color.fromRGB(
                        Integer.valueOf(value.substring(0, 2), 16),
                        Integer.valueOf(value.substring(2, 4), 16),
                        Integer.valueOf(value.substring(4, 6), 16));
            }
            return color;
        } catch (IllegalArgumentException ignored) {
            return Color.WHITE;
        }
    }

    /**
     * Converts "&c" format to a Color object containing RGB values
     *
     * @param value Must be in format "&0"
     * @return org.bukkit.Color
     */
    public static Color minecraft2Rgb(String value) {
        try {
            switch (value) {
                case "&0":
                    return Color.fromRGB(0, 0, 0);
                case "&1":
                    return Color.fromRGB(0, 0, 170);
                case "&2":
                    return Color.fromRGB(0, 170, 0);
                case "&3":
                    return Color.fromRGB(0, 170, 170);
                case "&4":
                    return Color.fromRGB(170, 0, 0);
                case "&5":
                    return Color.fromRGB(170, 0, 170);
                case "&6":
                    return Color.fromRGB(255, 170, 0);
                case "&7":
                    return Color.fromRGB(170, 170, 170);
                case "&8":
                    return Color.fromRGB(85, 85, 85);
                case "&9":
                    return Color.fromRGB(85, 85, 255);
                case "&a":
                    return Color.fromRGB(85, 255, 85);
                case "&b":
                    return Color.fromRGB(85, 255, 255);
                case "&c":
                    return Color.fromRGB(255, 85, 85);
                case "&d":
                    return Color.fromRGB(255, 85, 255);
                case "&e":
                    return Color.fromRGB(255, 255, 85);
                default:
                    return Color.fromRGB(255, 255, 255);
            }
        } catch (IllegalArgumentException ignored) {
            return Color.WHITE;
        }
    }
}