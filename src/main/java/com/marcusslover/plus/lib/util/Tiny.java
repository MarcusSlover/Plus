package com.marcusslover.plus.lib.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiny text utility class.
 */
public class Tiny {
    private static final Map<Character, Character> LETTERS = new HashMap<>() {{
        put('a', 'ᴀ');
        put('b', 'ʙ');
        put('c', 'ᴄ');
        put('d', 'ᴅ');
        put('e', 'ᴇ');
        put('f', 'ꜰ');
        put('g', 'ɢ');
        put('h', 'ʜ');
        put('i', 'ɪ');
        put('j', 'ᴊ');
        put('k', 'ᴋ');
        put('l', 'ʟ');
        put('m', 'ᴍ');
        put('n', 'ɴ');
        put('o', 'ᴏ');
        put('p', 'ᴘ');
        put('q', '\uA7AF');
        put('r', 'ʀ');
        put('s', 's');
        put('t', 'ᴛ');
        put('u', 'ᴜ');
        put('v', 'ᴠ');
        put('w', 'ᴡ');
        put('x', 'x');
        put('y', 'ʏ');
        put('z', 'ᴢ');

        put('0', '₀');
        put('1', '₁');
        put('2', '₂');
        put('3', '₃');
        put('4', '₄');
        put('5', '₅');
        put('6', '₆');
        put('7', '₇');
        put('8', '₈');
        put('9', '₉');
    }};

    private Tiny() {
    }

    /**
     * Converts a string to a tiny text string.
     *
     * @return The tiny text string.
     */
    public static @NotNull String of(@NotNull String text) {
        // turn all letters in text to tiny letters

        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            builder.append(LETTERS.getOrDefault(c, c));
        }
        return builder.toString();
    }
}
