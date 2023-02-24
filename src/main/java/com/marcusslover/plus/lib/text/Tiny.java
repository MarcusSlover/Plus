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
        LETTERS.put('a', '\u1D00');
        LETTERS.put('b', '\u0299');
        LETTERS.put('c', '\u1D04');
        LETTERS.put('d', '\u1D05');
        LETTERS.put('e', '\u1D07');
        LETTERS.put('f', '\uA730');
        LETTERS.put('g', '\u0262');
        LETTERS.put('h', '\u029C');
        LETTERS.put('i', '\u026A');
        LETTERS.put('j', '\u1D0A');
        LETTERS.put('k', '\u1D0B');
        LETTERS.put('l', '\u029F');
        LETTERS.put('m', '\u1D0D');
        LETTERS.put('n', '\u0274');
        LETTERS.put('o', '\u1D0F');
        LETTERS.put('p', '\u1D18');
        LETTERS.put('q', '\uA7AF');
        LETTERS.put('r', '\u0280');
        LETTERS.put('s', 's');
        LETTERS.put('t', '\u1D1B');
        LETTERS.put('u', '\u1D1C');
        LETTERS.put('v', '\u1D20');
        LETTERS.put('w', '\u1D21');
        LETTERS.put('x', 'x');
        LETTERS.put('y', '\u028F');
        LETTERS.put('z', '\u1D22');

        LETTERS.put('0', '\u2080');
        LETTERS.put('1', '\u2081');
        LETTERS.put('2', '\u2082');
        LETTERS.put('3', '\u2083');
        LETTERS.put('4', '\u2084');
        LETTERS.put('5', '\u2085');
        LETTERS.put('6', '\u2086');
        LETTERS.put('7', '\u2087');
        LETTERS.put('8', '\u2088');
        LETTERS.put('9', '\u2089');
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
