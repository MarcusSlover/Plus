package me.marcusslover.plus.lib.command;

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
    String name();
    String description() default "";
    String permission() default "";
    String[] aliases() default {};
}
