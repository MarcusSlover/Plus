package com.marcusslover.plus.lib.command;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for all kinds of implementations of {@link ICommand} interface.
 * This annotation contains data required for the command manager.
 * It's almost like a representation of what used to be put in plugin.yml
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    @NotNull String name();

    @NotNull String description() default "";

    @NotNull String permission() default "";

    @NotNull String permissionMessage() default "";

    @NotNull String[] aliases() default {};
}
