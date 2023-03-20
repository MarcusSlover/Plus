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

    /**
     * The name of the command. Also used as the label.
     *
     * @return The name of the command.
     */
    @NotNull String name();

    /**
     * The description of the command.
     *
     * @return The description of the command.
     */
    @NotNull String description() default "";

    /**
     * Permission required to execute the command.
     *
     * @return Permission required to execute the command.
     */
    @NotNull String permission() default "";

    /**
     * Permission message to send when the player doesn't have the permission.
     * If empty, no message will be sent. Supports color codes.
     *
     * @return Permission message to send when the player doesn't have the permission.
     */
    @NotNull String permissionMessage() default "";

    /**
     * Aliases of the command.
     *
     * @return Aliases of the command.
     */
    @NotNull String[] aliases() default {};
}
