package com.marcusslover.plus.lib.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiny text utility class.
 */
public class Tiny {
    private static final Map<Character, Character> LETTERS = new HashMap<>() {{
        put('a', '\u1D00');
        put('b', '\u0299');
        put('c', '\u1D04');
        put('d', '\u1D05');
        put('e', '\u1D07');
        put('f', '\uA730');
        put('g', '\u0262');
        put('h', '\u029C');
        put('i', '\u026A');
        put('j', '\u1D0A');
        put('k', '\u1D0B');
        put('l', '\u029F');
        put('m', '\u1D0D');
        put('n', '\u0274');
        put('o', '\u1D0F');
        put('p', '\u1D18');
        put('q', '\uA7AF');
        put('r', '\u0280');
        put('s', 's');
        put('t', '\u1D1B');
        put('u', '\u1D1C');
        put('v', '\u1D20');
        put('w', '\u1D21');
        put('x', 'x');
        put('y', '\u028F');
        put('z', '\u1D22');

        put('0', '\u2080');
        put('1', '\u2081');
        put('2', '\u2082');
        put('3', '\u2083');
        put('4', '\u2084');
        put('5', '\u2085');
        put('6', '\u2086');
        put('7', '\u2087');
        put('8', '\u2088');
        put('9', '\u2089');
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
