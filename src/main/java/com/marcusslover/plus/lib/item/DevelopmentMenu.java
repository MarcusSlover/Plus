package com.marcusslover.plus.lib.item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a development menu.
 * Experimental feature that lets you discover more debug information in the console.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DevelopmentMenu {
    /**
     * Whether the menu is a development menu.
     *
     * @return True to make the menu a development menu.
     */
    boolean value() default true;
}
