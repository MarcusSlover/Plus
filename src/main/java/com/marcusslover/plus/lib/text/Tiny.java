package com.marcusslover.plus.lib.text;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiny text utility class.
 */
public class Tiny {
    private static final Map<Character, Character> LETTERS = new HashMap<>();

    static {
        LETTERS.put('a', 'ᴀ');
        LETTERS.put('b', 'ʙ');
        LETTERS.put('c', 'ᴄ');
        LETTERS.put('d', 'ᴅ');
        LETTERS.put('e', 'ᴇ');
        LETTERS.put('f', 'ꜰ');
        LETTERS.put('g', 'ɢ');
        LETTERS.put('h', 'ʜ');
        LETTERS.put('i', 'ɪ');
        LETTERS.put('j', 'ᴊ');
        LETTERS.put('k', 'ᴋ');
        LETTERS.put('l', 'ʟ');
        LETTERS.put('m', 'ᴍ');
        LETTERS.put('n', 'ɴ');
        LETTERS.put('o', 'ᴏ');
        LETTERS.put('p', 'ᴘ');
        LETTERS.put('q', 'ǫ');
        LETTERS.put('r', 'ʀ');
        LETTERS.put('s', 's');
        LETTERS.put('t', 'ᴛ');
        LETTERS.put('u', 'ᴜ');
        LETTERS.put('v', 'ᴠ');
        LETTERS.put('w', 'ᴡ');
        LETTERS.put('x', 'x');
        LETTERS.put('y', 'ʏ');
        LETTERS.put('z', 'ᴢ');

        LETTERS.put('0', '₀');
        LETTERS.put('1', '₁');
        LETTERS.put('2', '₂');
        LETTERS.put('3', '₃');
        LETTERS.put('4', '₄');
        LETTERS.put('5', '₅');
        LETTERS.put('6', '₆');
        LETTERS.put('7', '₇');
        LETTERS.put('8', '₈');
        LETTERS.put('9', '₉');
    }

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
